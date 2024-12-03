package me.onlyjordon.pressed.quests.daily

import me.onlyjordon.pressed.quests.Quest
import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.entity.Player

class DailyKillstreakQuest: Quest() {
    override val name = "Killstreak (Daily)"
    override val levels = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15)
    override val isDaily = true

    init {
        setup()
    }

    override fun checkRequirement(player: Player): Int {
        val user = UserManager.getUser(player.uniqueId)
        levels.filter { !completeLevels.containsEntry(player.uniqueId, it) }.forEach {
            if (user.dailyKillstreak >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }

}