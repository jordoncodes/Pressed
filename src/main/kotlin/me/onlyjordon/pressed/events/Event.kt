package me.onlyjordon.pressed.events

import me.onlyjordon.pressed.stats.UserManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

abstract class Event {
    abstract val name: String
    abstract var world: World
    var currentWinners: MutableList<Player> = ArrayList()
    val players: MutableList<Player> = ArrayList()
    val eliminated: MutableList<Player> = ArrayList()

    var hasStarted: Boolean = false
    var hasEnded: Boolean = false

    fun start() {
        EventTimer(this).start()
        val msg = MiniMessage.miniMessage().deserialize("<light_purple>$name event starting!<dark_purple> <hover:show_text:'<light_purple>Click to Join!'><click:run_command:'/event $name join'>[<light_purple>/event ${name.toLowerCase()} join<dark_purple>]</click></hover><light_purple> to join event!")
        Bukkit.getOnlinePlayers().forEach { it.sendMessage(msg) }
    }

    fun endEvent() {
        hasEnded = true
        win(currentWinners)
        players.forEach {
            UserManager.getUser(it.uniqueId).respawn()
        }
        if (this is Listener) {
            HandlerList.unregisterAll(this)
        }
    }

    abstract fun join(player: Player)

    abstract fun win(players: List<Player>)

    abstract fun timerFinished()
    abstract fun quit(player: Player)
}