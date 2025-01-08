package me.onlyjordon.pressed.events.tntrun

import me.onlyjordon.pressed.events.Event
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.md_5.bungee.api.ChatColor
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.stream.Collectors

class TntRunEvent : Event(), Listener {
    override var world: World = Bukkit.getWorld("tnt-run")!!
    override var resetMap = true
    var pvpRun = false
    override fun join(player: Player) {
        players.add(player)
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have joined the &5$simpleName&d event!"))
        player.teleport(tntRunSpawn.random())
    }

    override fun quit(player: Player) {
        if (isActivePlayer(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot do this right now!"))
            return
        }
        players.remove(player)
        UserManager.getUser(player.uniqueId).respawn()
    }

    val tntRunSpawn: MutableList<Location> = mutableListOf(Location(world, -10.0, 68.0, 13.0, -141f, 0f), Location(world, 11.5, 67.0, 13.0, 138f, 0f), Location(world, 15.5, 67.0, -12.5, 50f, 0f), Location(world, -6.5, 65.0, -13.5, -30f, 0f))
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
    fun onPlace(event: BlockPlaceEvent) {
        if (players.contains(event.player)) {
            event.isCancelled = true
        }
    }

    fun die(player: Player) {
        player.teleport(tntRunSpawn.random())
        activePlayers.remove(player)
        eliminated.add(player)
        if (activePlayers.size <= 1) {
            var winner: Player? = activePlayers.firstOrNull()
            if (winner != null) {
                currentWinners.add(winner)
                endEvent()
            } else {
                endEvent()
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (isActivePlayer(event.player)) {
            die(event.player)
        }
        players.remove(event.player)
        activePlayers.remove(event.player)
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
        if (!activePlayers.contains(victim)) return
        if (event.cause == EntityDamageEvent.DamageCause.LAVA) {
            die(victim)
        }
    }



    override fun win(players: List<Player>) {
        players.forEach {
            val user = UserManager.getUser(it.uniqueId)
            user.coins+=(250 * plugin.currentGlobalBooster).toLong()
            user.xp+=(250 * plugin.currentGlobalBooster).toLong()
            user.tntRunEventWins++
            it.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have won the &5$simpleName &devent! &5+250&e Coins &dand &5+250xp"))
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5$simpleName &devent is over! Winner: ${players.stream().map { it.name }.collect(Collectors.joining(", "))}"))
    }

    var activePlayers: MutableSet<Player> = mutableSetOf()

    override fun timerFinished() {
//        activePlayers = pickNewPlayers(null)
//        if (activePlayers == null) return
//        startSumoMatch()
        state = TntRunState.PLAYING
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