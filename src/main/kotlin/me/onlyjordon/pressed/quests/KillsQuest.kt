package me.onlyjordon.pressed.quests

import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.entity.Player

class KillsQuest: Quest() {
    override val name = "Kills"
    override val levels = listOf(1,5,10,20, 50, 100, 150, 200, 250, 300, 400, 500, 750, 1000, 1250, 1500, 1750, 2000, 2500, 3000, 3500, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12500, 15000, 17500, 20000)

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
            if (user.kills >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }
}