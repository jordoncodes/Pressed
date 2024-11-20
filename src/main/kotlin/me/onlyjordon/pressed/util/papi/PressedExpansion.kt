package me.onlyjordon.pressed.util.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.OfflinePlayer

class PressedExpansion: PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "pressed"
    }

    override fun getAuthor(): String {
        return "onlyjordon"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player != null) {
            var s = ""
            val online = player.isOnline
            val user = UserManager.getUser(player.uniqueId)
            if (params == "kills") {
                s = user.kills.toString()
            }
            if (params == "deaths") {
                s = user.deaths.toString()
            }
            if (params == "killstreak") {
                s = user.killstreak.toString()
            }
            if (params == "highestKillstreak") {
                s = user.highestKillstreak.toString()
            }
            if (params == "coins") {
                s = user.coins.toString()
            }
            if (params == "xp") {
                s = user.xp.toString()
            }
            if (params == "nick") {
//                s = NickListener.nameMap.getOrDefault(player.uniqueId, player.name).toString()
                s = player.name.toString()
            }
            if (!online) {
                user.save()
                UserManager.clear(user)
            }
            return s
        }
        return super.onRequest(player, params)
    }

    companion object {
        fun registerHook() {
            PressedExpansion().register()
        }

        fun unregisterHook() {
            PressedExpansion().unregister()
        }
    }

}