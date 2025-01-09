package me.onlyjordon.pressed.events.tntrun

import me.onlyjordon.pressed.events.Event
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.md_5.bungee.api.ChatColor
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.awt.Color
import java.util.stream.Collectors
import javax.xml.transform.Result
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

class TntRunEvent : Event(), Listener {
    override var world: World = Bukkit.getWorld("tnt-run")!!
    override var resetMap = true
    var pvpRun = false
    var hiddenPairs: MutableList<Pair<Player,Player>> = mutableListOf()
    override fun join(player: Player) {
        players.add(player)
        activePlayers.add(player)
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have joined the &5$simpleName&d event!"))
        player.teleport(tntRunSpawn.random())
    }

    override fun quit(player: Player) {
        if (isActivePlayer(player)) {
            die(player)
        }
        players.remove(player)
        UserManager.getUser(player.uniqueId).respawn()
        player.allowFlight = false
        player.fireTicks = 0
    }

    val tntRunSpawn: MutableList<Location>
        get() = mutableListOf(Location(world, 0.0, 91.5, 0.0, 0f, 0f))
    var state = TntRunState.WAITING
    override val name = "tnt-run"
    val simpleName = "TNT Run"

    fun isActivePlayer(player: Player): Boolean {
        return activePlayers.contains(player)
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (players.contains(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player.world != world) return
        if (eliminated.contains(event.player)) event.isCancelled = true
        if (state != TntRunState.PLAYING) event.isCancelled = true
        if (event.action == Action.PHYSICAL) {
            if (event.clickedBlock!!.type == Material.STONE_PRESSURE_PLATE) {
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY)
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player.world != world) return
        if (state != TntRunState.PLAYING) return
        if (eliminated.contains(event.player)) return

        // ping adjustment
        val ticks = (max(11.0 - event.player.ping / 50.0, 1.0)).roundToLong()
        // cant stay on edge of block
        val blocks = listOf(-0.5 to -0.5, 0.5 to -0.5, -0.5 to 0.5, 0.5 to 0.5, 0.5 to 0.0, -0.5 to 0.0, 0.0 to 0.5, 0.0 to -0.5, 0.0 to 0.0)
        blocks.forEach { (x, z) ->
            val playerLoc = event.to.clone().add(0.0, 0.0, 0.0)
            playerLoc.world.spawnParticle(Particle.DUST, playerLoc.clone().add(0.0, 2.0, 0.0), 1, Particle.DustOptions(org.bukkit.Color.YELLOW, 5f))
            val newBlock = playerLoc.clone().add(x,0.0,z).block
            val loc = newBlock.getRelative(BlockFace.UP).location.add(0.5, 0.0, 0.5)
            loc.world.spawnParticle(Particle.DUST, loc, 1, Particle.DustOptions(org.bukkit.Color.RED, 5f))
            if (newBlock.getRelative(BlockFace.DOWN).type.hasGravity()) {
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    if (newBlock.chunk.isLoaded)
                        newBlock.type = Material.AIR
                    newBlock.getRelative(BlockFace.DOWN).type = Material.AIR
                    newBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type = Material.AIR
                }, ticks)
            }
        }
//        if (b.getRelative(BlockFace.DOWN).type.hasGravity()) {
//            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
//                if (b.chunk.isLoaded)
//                b.type = Material.AIR
//                b.getRelative(BlockFace.DOWN).type = Material.AIR
//                b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).type = Material.AIR
//            }, ticks)
//        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        if (players.contains(event.player)) {
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onPrime(event: TNTPrimeEvent) {
        if (event.block.world == world) {
            event.isCancelled = true
            event.block.type = Material.AIR
        }
    }

    fun die(player: Player) {
        player.teleport(tntRunSpawn.random())
        eliminated.add(player)
        activePlayers.remove(player)
        player.allowFlight = true
        player.isFlying = true
        if (activePlayers.size <= 1 && !hasEnded) {
            var winner: Player? = activePlayers.firstOrNull()
            hiddenPairs.forEach { (alive, player) ->
                alive.showPlayer(plugin, player)
            }
            if (winner != null) {
                currentWinners.add(winner)
                endEvent()
            } else {
                endEvent()
            }
        }
        if (!hasEnded) {
            activePlayers.forEach { alive ->
                alive.hidePlayer(plugin, player)
                hiddenPairs.add(alive to player)
                hiddenPairs.forEach { (_, de) ->
                    player.showPlayer(plugin, de)
                }
            }
        }
        player.fireTicks = 0
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (isActivePlayer(event.player)) {
            die(event.player)
        }
        players.remove(event.player)
        activePlayers.remove(event.player)
        event.player.allowFlight = false
    }

    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        onQuit(PlayerQuitEvent(event.player, ""))
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity
        val attacker = event.damager
        if (victim !is Player || attacker !is Player) return
        if (players.contains(victim) && (!isActivePlayer(victim))) event.isCancelled = true
        when (state) {
            TntRunState.WAITING -> {
                if (isActivePlayer(victim)) {
                    event.isCancelled = true
                }
            }
            TntRunState.PLAYING -> {
                if (isActivePlayer(victim)) {
                    if (pvpRun)
                        event.damage = 0.00000001
                    else event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val victim = event.entity
        if (victim !is Player) return
        if (eliminated.contains(event.entity) && event.entity.world == world) {
            event.isCancelled = true
            return
        }
        if (!activePlayers.contains(victim)) return
        println("damage to player!")
        if (event.cause == EntityDamageEvent.DamageCause.LAVA) {
            println("dying to lava")
            die(victim)
        }
    }



    override fun win(players: List<Player>) {
        players.forEach {
            val user = UserManager.getUser(it.uniqueId)
            user.coins+=(250 * plugin.currentGlobalBooster).toLong()
            user.xp+=(250 * plugin.currentGlobalBooster).toLong()
            user.tntRunEventWins++
            it.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have won the &5$simpleName &devent! &5+${(250 * plugin.currentGlobalBooster).toLong()}&e Coins &dand &5+${(250 * plugin.currentGlobalBooster).toLong()}xp"))
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5$simpleName &devent is over! Winner: ${players.stream().map { it.name }.collect(Collectors.joining(", "))}"))
    }

    var activePlayers: MutableSet<Player> = mutableSetOf()

    override fun timerFinished() {
//        activePlayers = pickNewPlayers(null)
//        if (activePlayers == null) return
//        startSumoMatch()
        state = TntRunState.PLAYING
        activePlayers.forEach { player ->
            onMove(PlayerMoveEvent(player, player.location, player.location))
        }
    }
//
//    fun startSumoMatch() {
//        activePlayers?.first?.teleport(pos1) ?: endEvent()
//        activePlayers?.second?.teleport(pos2) ?: endEvent()
//        state = TntRunState.STARTING
//        TimerRunnable(this).runTaskTimer(plugin, 20, 20)
//    }

//    fun pickNewPlayers(previousWinner: Player?): Pair<Player, Player>? {
//        var winner = if (previousWinner?.isOnline == true) previousWinner else null
//        try {
//            val player2 = players.filter { it != winner }.filter { p -> !eliminated.contains(p) }.random()
//            val player1 = winner ?: players.filter { p -> !eliminated.contains(p) }.filter { p -> p != player2 }.random()
//            return player1 to player2
//        } catch (ex: NoSuchElementException) {
//            endEvent()
//        }
//        return null
//    }

    class TimerRunnable(val tntRunEvent: TntRunEvent): BukkitRunnable() {
        var i = 4
        override fun run() {
            i--
            if (i == 0) {
                tntRunEvent.players.forEach {
                    it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lGO"), "", 0, 20, 0)
                }
                tntRunEvent.state = TntRunState.PLAYING
                cancel()
                return
            }
            tntRunEvent.players.forEach {
                it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&5${tntRunEvent.simpleName} &dstarting in"), ChatColor.translateAlternateColorCodes('&', "&5$i &dseconds..."), 0, 40, 0)
            }
        }

    }

    enum class TntRunState {
        WAITING,
        PLAYING
    }

}