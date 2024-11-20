package me.onlyjordon.pressed.util

import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class GlobalMultiTimer: BukkitRunnable() {

    val map = mutableMapOf<UUID, Pair<Double,Long>>()

    override fun run() {
        plugin.globalMultiConfig.getConfigurationSection("current")?.getKeys(false)?.forEach { idString ->
            var amt = 0.0
            var time = 0L
            plugin.globalMultiConfig.getConfigurationSection("current.$idString")?.getKeys(false)?.forEach { key ->
                if (key == "time") {
                    time = plugin.globalMultiConfig.getLong("current.$idString.$key")
                }
                if (System.currentTimeMillis() > time) {
                    plugin.globalMultiConfig.set("current.$idString", null)
                }
                if (key == "amount") {
                    amt = plugin.globalMultiConfig.getDouble("current.$idString.$key")
                }
            }
            map[UUID.fromString(idString)] = amt to time
        }


    }
}