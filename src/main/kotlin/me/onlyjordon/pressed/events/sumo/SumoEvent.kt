package me.onlyjordon.pressed.events.sumo

import me.onlyjordon.pressed.events.Event
import me.onlyjordon.pressed.events.tntrun.TntRunEvent
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.md_5.bungee.api.ChatColor
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
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.stream.Collectors

class SumoEvent : Event(), Listener {
    override var world: World = Bukkit.getWorld("sumo")!!
    override fun join(player: Player) {
        players.add(player)
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have joined the &5$name&d event!"))
        player.teleport(sumoSpawn.random())
    }

    override fun quit(player: Player) {
        if (isActivePlayer(player)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot do this right now!"))
            return
        }
        players.remove(player)
        UserManager.getUser(player.uniqueId).respawn()
    }

    val sumoSpawn: MutableList<Location> = mutableListOf(Location(world, -10.0, 68.0, 13.0, -141f, 0f), Location(world, 11.5, 67.0, 13.0, 138f, 0f), Location(world, 15.5, 67.0, -12.5, 50f, 0f), Location(world, -6.5, 65.0, -13.5, -30f, 0f))
    val pos1: Location = Location(world, 0.5, 65.0, -3.5, 0f, 0f)
    val pos2: Location = Location(world, 0.5, 65.0, 4.5, 180f, 0f)
    var state = SumoState.WAITING
    override val name = "Sumo"

    fun isActivePlayer(player: Player): Boolean {
        return player == activePlayers?.first || player == activePlayers?.second
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

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        when (state) {
            SumoState.WAITING -> {}
            SumoState.STARTING -> {
                if (isActivePlayer(player))
                    event.isCancelled = true
            }
            SumoState.PLAYING -> {
                if ((event.to.y < (pos1.y-2) || event.to.y < (pos2.y-2)) && isActivePlayer(player)) {
                    die(player)
                }
            }
        }
    }

    fun die(player: Player) {
        activePlayers?.first?.teleport(sumoSpawn.random())
        activePlayers?.second?.teleport(sumoSpawn.random())
        state = SumoState.WAITING
        var winner: Player? = null
        (if (activePlayers?.first == player) activePlayers?.second else activePlayers?.first)?.let {
            currentWinners.clear()
            currentWinners.add(it)
            winner = it
        }
        eliminated.add(player)
        activePlayers = pickNewPlayers(winner)
        if (activePlayers == null) return
        startSumoMatch()
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val victim = event.entity
        if (victim !is Player) return
        if (players.contains(victim) && !isActivePlayer(victim)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (isActivePlayer(event.player)) {
            die(event.player)
        }
        players.remove(event.player)
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
            SumoState.WAITING -> {
                if (isActivePlayer(victim)) {
                    event.isCancelled = true
                }
            }
            SumoState.STARTING -> {
                if (isActivePlayer(victim)) {
                    event.isCancelled = true
                }
            }
            SumoState.PLAYING -> {
                if (isActivePlayer(victim)) {
                    event.damage = 0.00000001
                }
            }
        }
    }


    override fun win(players: List<Player>) {
        players.forEach {
            val user = UserManager.getUser(it.uniqueId)
            user.coins+=(250 * plugin.currentGlobalBooster).toLong()
            user.xp+=(250 * plugin.currentGlobalBooster).toLong()
            user.sumoEventWins++
            it.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dYou have won the &5$name &devent! &5+${250 * plugin.currentGlobalBooster}&e Coins &dand &5+${250 * plugin.currentGlobalBooster}xp"))
        }
        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5$name &devent is over! Winner: ${players.stream().map { it.name }.collect(Collectors.joining(", "))}"))
    }

    var activePlayers: Pair<Player, Player>? = null

    override fun timerFinished() {
        activePlayers = pickNewPlayers(null)
        if (activePlayers == null) return
        startSumoMatch()
    }

    fun startSumoMatch() {
        activePlayers?.first?.teleport(pos1) ?: endEvent()
        activePlayers?.second?.teleport(pos2) ?: endEvent()
        state = SumoState.STARTING
        TimerRunnable(this).runTaskTimer(plugin, 20, 20)
    }

    fun pickNewPlayers(previousWinner: Player?): Pair<Player, Player>? {
        var winner = if (previousWinner?.isOnline == true) previousWinner else null
        try {
            val player2 = players.filter { it != winner }.filter { p -> !eliminated.contains(p) }.random()
            val player1 = winner ?: players.filter { p -> !eliminated.contains(p) }.filter { p -> p != player2 }.random()
            return player1 to player2
        } catch (ex: NoSuchElementException) {
            endEvent()
        }
        return null
    }

    class TimerRunnable(val sumoEvent: SumoEvent): BukkitRunnable() {
        var i = 4
        override fun run() {
            i--
            if (i == 0) {
                sumoEvent.players.forEach {
                    it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lGO"), "", 0, 20, 0)
                }
                sumoEvent.state = SumoState.PLAYING
                cancel()
                return
            }
            sumoEvent.players.forEach {
                it.sendTitle(ChatColor.translateAlternateColorCodes('&', "&5${sumoEvent.name} &dstarting in"), ChatColor.translateAlternateColorCodes('&', "&5$i &dseconds..."), 0, 40, 0)
            }
        }

    }

    enum class SumoState {
        WAITING,
        STARTING,
        PLAYING
    }

}