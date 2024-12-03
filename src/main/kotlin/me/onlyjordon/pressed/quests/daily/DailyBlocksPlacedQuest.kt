package me.onlyjordon.pressed.quests.daily

import me.onlyjordon.pressed.quests.Quest
import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.entity.Player

class DailyBlocksPlacedQuest: Quest() {
    override val name = "Blocks Placed (Daily)"
    override val levels = listOf(25, 50, 75, 100, 200, 250, 275, 300, 350, 400, 500, 1000)
    override val isDaily = true

    init {
        setup()
    }

    override fun checkRequirement(player: Player): Int {
        val user = UserManager.getUser(player.uniqueId)
        levels.filter { !completeLevels.containsEntry(player.uniqueId, it) }.forEach {
            if (user.dailyBlocksPlaced >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }

}