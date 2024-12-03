package me.onlyjordon.pressed.quests

import com.google.common.collect.HashMultimap
import me.onlyjordon.pressed.stats.UserManager
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.log
import kotlin.math.roundToLong

class KillstreakQuest: Quest() {
    override val name = "Killstreak"
    override val levels = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16, 18, 20, 25, 30, 35, 40, 50, 60, 70, 80, 90, 100)

    init {
        setup()
    }

    override fun checkRequirement(player: Player): Int {
        val user = UserManager.getUser(player.uniqueId)
        levels.filter { !completeLevels.containsEntry(player.uniqueId, it) }.forEach {
            if (user.killstreak >= it) {
                return levels.indexOf(it)
            }
        }
        return -1
    }

}