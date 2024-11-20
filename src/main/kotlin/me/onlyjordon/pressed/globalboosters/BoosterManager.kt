package me.onlyjordon.pressed.globalboosters
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class BoosterManager {
    val activeBooster: Booster
        get() = activeBoosters.first()
    val activeBoosters: MutableList<Booster> = mutableListOf()

    fun applyBooster(ownerUUID: UUID, boosterValue: Double, duration: Long) {
        val newBooster = Booster(ownerUUID, boosterValue, System.currentTimeMillis() + duration)
        activeBoosters.add(newBooster)

        applyActiveBoosters()
    }

    fun removeExpiredBoosters() {
        val expiredBoosters = activeBoosters.filter { it.remainingTime <= 0 }
        expiredBoosters.forEach { booster ->
            activeBoosters.remove(booster)
        }
        applyActiveBoosters()
    }

    fun applyActiveBoosters() {
        activeBoosters.sortByDescending { it.multiplier }

        for (i in 1 until activeBoosters.size) {
            activeBoosters[i].pause()
        }
        if (activeBoosters.isEmpty()) {
            plugin.currentGlobalBooster = 1.0
            return
        }
        val activeBooster = activeBoosters.first()

        if (activeBooster.paused) {
            activeBooster.resume()
        }

        val totalMultiplier = activeBooster.multiplier
        applyMultiplierToAllPlayers(totalMultiplier)
    }

    private fun applyMultiplierToAllPlayers(multiplier: Double) {
        plugin.currentGlobalBooster = multiplier
    }

    fun pauseBooster(booster: Booster) {
        booster.pause()
        applyActiveBoosters()
    }

    fun resumeBooster(booster: Booster) {
        booster.resume()
        applyActiveBoosters()
    }

    fun startBoosterCleanupTask() {
        object : BukkitRunnable() {
            override fun run() {
                removeExpiredBoosters()
            }
        }.runTaskTimer(plugin, 20L, 20L)
    }
}
