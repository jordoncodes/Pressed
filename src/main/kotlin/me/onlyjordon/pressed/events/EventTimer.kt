package me.onlyjordon.pressed.events

import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class EventTimer(val event: Event): BukkitRunnable() {
    var timeLeft = 60
    override fun run() {
        if (timeLeft % 10 == 0 || (timeLeft < 30 && timeLeft % 5 == 0) || (timeLeft < 11)) {
            Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize("<hover:show_text:'<light_purple>Click to Join!'><click:run_command:'/event ${event.name} join'><light_purple>Starting <dark_purple>${event.name}</dark_purple> in <dark_purple>$timeLeft</dark_purple> seconds. Click to join!</click></hover>"))
        }
        if (timeLeft == 0) {
            event.hasStarted = true
            event.timerFinished()
            cancel()
            return
        }
        timeLeft--

    }

    fun start() {
        runTaskTimer(plugin, 0L, 20L)
    }

}