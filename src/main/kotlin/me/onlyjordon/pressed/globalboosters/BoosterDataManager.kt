package me.onlyjordon.pressed.globalboosters
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.util.*

class BoosterDataManager {

    private val config: FileConfiguration = plugin.globalMultiConfig

    fun saveBoosters(activeBoosters: List<Booster>) {
        activeBoosters.forEachIndexed { index, booster ->
            val playerPath = "boosters.$index"
            config.set("$playerPath.ownerUUID", booster.ownerUUID.toString())
            config.set("$playerPath.multiplier", booster.multiplier)
            config.set("$playerPath.endTime", booster.remainingTime)
            config.set("$playerPath.paused", !booster.isActive)
        }
        saveConfig()
    }

    fun loadBoosters(activeBoosters: MutableList<Booster>) {
        val section = config.getConfigurationSection("boosters") ?: return
        section.getKeys(false).forEach { index ->
            val ownerUUID = UUID.fromString(config.getString("boosters.$index.ownerUUID") ?: return@forEach)
            val multiplier = config.getDouble("boosters.$index.multiplier")
            val endTime = config.getLong("boosters.$index.endTime")
            val paused = config.getBoolean("boosters.$index.paused")

            val booster = Booster(ownerUUID, multiplier, endTime + System.currentTimeMillis())
            if (paused) {
                booster.pause()
            }
            activeBoosters.add(booster)
        }
    }

    private fun saveConfig() {
        try {
            config.save(plugin.globalMultiConfigFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
