package me.onlyjordon.pressed.tasks

import me.onlyjordon.pressed.util.BlockManager
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

class GameLoop: BukkitRunnable() {
    companion object {

    }

    var savedHour = 25

    override fun run() {
//        BlockManager.getBlocks().forEach { block ->
//            block.key.world.spawnParticle(Particle.DUST, block.key.clone().add(0.5, 2.0, 0.5), 1, 0.0, 0.0, 0.0, 1.0, Particle.DustOptions(Color.RED, 5.0f))
//        }
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour != savedHour) {
            savedHour = hour
            plugin.regenerateShopItems()
        }
    }

    override fun cancel() {
        BlockManager.removeAll()
        super.cancel()
    }
}