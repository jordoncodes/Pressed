package me.onlyjordon.pressed.util

import me.onlyjordon.pressed.tasks.BlockDecayTimer
import me.onlyjordon.pressed.util.UsefulFunctions.asPlayer
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import me.onlyjordon.pressed.util.UsefulFunctions.toLocation
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*

object BlockManager {

    private val blocks = HashMap<Triple<Int, Int, Int>, UUID>()

    fun contains(block: Block): Boolean {
        return blocks.containsKey(Triple(block.x, block.y, block.z))
    }

    fun remove(block: Block) {
        blocks.remove(Triple(block.x, block.y, block.z))
    }

    fun add(block: Block, player: UUID) {
        blocks[Triple(block.x, block.y, block.z)] = player
        BlockDecayTimer(player, block).runTaskTimer(plugin, 1L, 1L)
    }

    fun removeAll(uniqueId: UUID) {
        blocks.filter { it.value == uniqueId }.keys.toList().forEach {
            it.toLocation(plugin.world).block.type = Material.AIR
            blocks.remove(it)
        }
    }

    fun removeAll() {
        blocks.keys.toList().forEach {
            it.toLocation(plugin.world).block.type = Material.AIR
            blocks.remove(it)
        }
    }

    fun getBlocks(): Map<Location, Player?> {
        return blocks.mapKeys {
            it.key.toLocation(plugin.world)
        }.mapValues {
            it.value.asPlayer
        }
    }

}