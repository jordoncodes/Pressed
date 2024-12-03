package me.onlyjordon.pressed.quests.daily

import me.onlyjordon.pressed.quests.Quest
import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.entity.Player

class DailyKillsQuest: Quest() {
    override val name = "Kills (Daily)"
    override val levels = listOf(1, 2, 4, 8, 10, 12, 14, 16, 18, 20, 25, 50, 100)
    override val isDaily = true

    init {
        setup()
    }

    override fun checkRequirement(player: Player): Int {
        val user = UserManager.getUser(player.uniqueId)
        levels.filter { !completeLevels.containsEntry(player.uniqueId, it) }.forEach {
            if (user.dailyKills >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }
}