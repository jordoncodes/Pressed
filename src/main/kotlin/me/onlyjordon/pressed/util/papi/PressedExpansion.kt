package me.onlyjordon.pressed.util.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import java.util.concurrent.TimeUnit

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
            if (params == "koth-capture-status") {
                val koth = UsefulFunctions.plugin.kothArea
                val highest = koth.captureProgress.maxByOrNull { it.value }
                if (highest == null) {
                    s = ChatColor.RED.toString() + "No one is capturing!"
                } else {
                    s = "${highest.key.name} is capturing! ${getProgressBar(highest.value, koth.captureTimeSeconds)}"
                }
                if (koth.captureProgress.size > 1) {
                    s = ChatColor.RED.toString() + "Multiple people are capturing!"
                }
                if (!koth.isActive) {
                    s = "Inactive"
                }
            }
            if (params == "koth-owner") {
                val koth = UsefulFunctions.plugin.kothArea
                if (koth.currentOwner == null) {
                    s = ChatColor.RED.toString() + "Current Owner: No one!"
                } else {
                    s = ChatColor.GREEN.toString() + "Captured by " + koth.currentOwner!!.name
                }
                if (!koth.isActive) {
                    s = ChatColor.RED.toString() + "Current Owner: No one!"
                }
            }
            if (params == "shop-timer") {
                val savedHour = UsefulFunctions.plugin.gameLoop.savedTime
                val elapsed = System.currentTimeMillis() - savedHour
                val countdownMillis = TimeUnit.MINUTES.toMillis(60)
                val remainingMillis = countdownMillis - elapsed
                val mins = TimeUnit.MILLISECONDS.toMinutes(remainingMillis)
                var time = ""
                if (mins <= 0) {
                    time = String.format(
                        "%dmin, %dsec",
                        TimeUnit.MILLISECONDS.toMinutes(remainingMillis),
                        TimeUnit.MILLISECONDS.toSeconds(remainingMillis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingMillis))
                    )
                } else {
                    time = String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(remainingMillis),
                        TimeUnit.MILLISECONDS.toSeconds(remainingMillis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingMillis))
                    )
                }
                s = time
            }
            if (!online) {
                user.save()
                UserManager.clear(user)
            }
            return s
        }
        return super.onRequest(player, params)
    }


    fun getProgressBar(current: Int, total: Int): String {
        val progressBarLength = 10 // Total number of "bars"
        val progress = (current.toDouble() / total * progressBarLength).toInt() // Calculate completed bars
        val completed = "&a" + "||".repeat(progress) // Green part for completed
        val remaining = "&c" + "||".repeat(progressBarLength - progress) // Red part for remaining
        return completed + remaining
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