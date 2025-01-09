package me.onlyjordon.pressed.stats

import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import org.bukkit.Bukkit
import java.util.*

object UserManager {

    val userMap = HashMap<UUID, User>()

    fun containsUser(uniqueId: UUID): Boolean {
        return userMap.containsKey(uniqueId)
    }

    fun getUser(uniqueId: UUID): User {
        var user = userMap[uniqueId]
        if (user == null) {
            user = User(Bukkit.getOfflinePlayer(uniqueId))
            userMap[uniqueId] = user
        }
        return user
    }

    fun removeUser(uniqueId: UUID) {
        userMap.remove(uniqueId)
    }

    fun clear(user: User) {
        userMap.remove(user.player.uniqueId)
    }

    fun count(): Int {
        return userMap.size
    }

}