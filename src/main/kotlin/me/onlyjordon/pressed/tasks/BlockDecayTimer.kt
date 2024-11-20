package me.onlyjordon.pressed.tasks

import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.BlockManager
import me.onlyjordon.pressed.util.UsefulFunctions.asPlayer
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class BlockDecayTimer(val player: UUID, val block: Block): BukkitRunnable() {
    val cosmetic = UserManager.getUser(player).blockType
    var ticks = 0L
    override fun run() {
        ticks++
        val count = cosmetic.blocks.count()
        val timeBetweenBlocks = 120L/count.toLong()
        if (ticks == timeBetweenBlocks * count) {
            block.type = Material.AIR
            BlockManager.remove(block)
            cancel()
            return
        }
        if ((ticks % timeBetweenBlocks) == 0.toLong()) {
            if (!BlockManager.contains(block)) {
                cancel()
                return
            }
            val stage = ticks/timeBetweenBlocks
            if (block.type != Material.COBWEB) block.type = cosmetic.blocks[stage.toInt()].material
        }
    }

    override fun cancel() {
        block.type = Material.AIR
        super.cancel()
    }
}