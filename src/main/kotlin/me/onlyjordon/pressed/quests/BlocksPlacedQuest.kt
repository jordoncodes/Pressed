package me.onlyjordon.pressed.quests

import com.google.common.collect.HashMultimap
import me.onlyjordon.pressed.stats.UserManager
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.log
import kotlin.math.roundToLong

class BlocksPlacedQuest: Quest() {
    override val name = "Blocks Placed"
    override val levels = listOf(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1200, 1400, 1600, 1800, 2000, 2500, 3000, 3500, 4000, 5000, 6000, 7000, 8000, 9000, 10000)

    init {
        setup()
    }

    override fun checkRequirement(player: Player): Int {
        val user = UserManager.getUser(player.uniqueId)
        if (!completeLevels.containsKey(player.uniqueId)) {
            unload(player.uniqueId)
            load(player.uniqueId)
        }
        levels.filter { !completeLevels.containsEntry(player.uniqueId, it) }.forEach {
            if (user.blocksPlaced >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }

}