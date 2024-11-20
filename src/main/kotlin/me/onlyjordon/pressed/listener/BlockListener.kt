package me.onlyjordon.pressed.listener

import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.BlockManager
import me.onlyjordon.pressed.util.UsefulFunctions.isInArena
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockListener: Listener {
    @EventHandler
    fun placeBlock(event: BlockPlaceEvent) {
        if (event.player.gameMode == GameMode.CREATIVE) return
        val loc = event.block.location
        loc.y += 10
        if (!isInArena(loc)) {
            event.isCancelled = true
            return
        }
        if (event.block.type != Material.COBWEB) {
            val user = UserManager.getUser(event.player.uniqueId)
            val handItem = event.player.inventory.getItem(event.hand)
            if (handItem == user.stick.stack) {
                event.isCancelled = true
                return
            }
            if (handItem.type == event.block.type) {
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    handItem.amount = 64
                }, 2L)
            }
        }
        BlockManager.add(event.block, event.player.uniqueId)
        UserManager.getUser(event.player.uniqueId).blocksPlaced++
        plugin.blocksPlacedQuest.checkRequirementAndComplete(event.player)
    }

    @EventHandler
    fun breakBlock(event: BlockBreakEvent) {
        if (event.player.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
            return
        }
        BlockManager.remove(event.block)
    }
    
}