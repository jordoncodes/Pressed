package me.onlyjordon.pressed.util

import fr.mrmicky.fastboard.FastBoard
import me.onlyjordon.pressed.Pressed
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions.color
import me.onlyjordon.pressed.util.UsefulFunctions.isInArena
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit


class BoardManager: Listener {
    private val boards = HashMap<UUID, FastBoard>()
    private val plugin = JavaPlugin.getPlugin(Pressed::class.java)

    fun setup(): BoardManager {
        Bukkit.getServer().pluginManager.registerEvents(this, plugin)

        Bukkit.getServer().scheduler.runTaskTimer(plugin, Runnable {
            for (board in boards.values) {
                updateBoard(board)
            }
        }, 0, 10)
        return this
    }


    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        val board = object : FastBoard(player) {
            override fun hasLinesMaxLength(): Boolean {
                return true
            }
        }

        board.updateTitle(color("&f&l&k|&f&l -&5&lPRESSED&f&l- &f&k|"))

        boards[player.uniqueId] = board
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player

        val board = boards.remove(player.uniqueId)

        board?.delete()
    }

    private fun updateBoard(board: FastBoard) {

        val u = UserManager.getUser(board.player.uniqueId)

        var statusMessage = "&aSafe"
        if (isInArena(board.player.location)) statusMessage = "&fArena"
        if (u.killer.second > System.currentTimeMillis()) {
            statusMessage = "&cCombat (${TimeUnit.MILLISECONDS.toSeconds(u.killer.second - System.currentTimeMillis())}s)"
        }
        val list = mutableListOf(color("&d&m----------------------"),
            color("&5Players&f: &d" + Bukkit.getServer().onlinePlayers.size),
            "",
            color("&5K/D&7: &d" + NumberFormat.getNumberInstance().format(u.kills) + "&f/&d" + NumberFormat.getNumberInstance().format(u.deaths)),
            color("&5Streak&7: &d" + NumberFormat.getNumberInstance().format(u.killstreak) + " &f(&5" + NumberFormat.getNumberInstance().format(u.highestKillstreak) + "&f)"),
            color("&5Coins&7: &d" + NumberFormat.getNumberInstance().format(u.coins))
//            color("&5XP&7: &d" + NumberFormat.getNumberInstance().format(u.xp))
        )

        if (u.blazeDust > 0) {
            list.add(color("&5Blaze Dust&7: &d" + NumberFormat.getNumberInstance().format(u.blazeDust)))
        }
        list.add(color("&d$statusMessage"))

        if (plugin.currentGlobalBooster > 1 && plugin.boosterManager.activeBoosters.size > 0) {
            val seconds: Long = plugin.boosterManager.activeBooster.remainingTime / 1000
            val DD: Long = seconds / 86400
            val HH: Long = (seconds % 86400) / 3600
            val MM: Long = (seconds % 3600) / 60
            val SS: Long = seconds % 60
            val includeDays = DD > 0
            val includeHours = HH > 0 || includeDays
            list.addAll(listOf(
                "",
                color("&5Booster"),
                color("&d${NumberFormat.getNumberInstance().format(plugin.currentGlobalBooster)}&5x by &d${(Bukkit.getOfflinePlayer(plugin.boosterManager.activeBooster.ownerUUID).name ?: "Unknown Player")}"),
                color("&d" + if (includeDays) String.format("%02d days %02d:%02d:%02d", DD, HH, MM, SS) else if (includeHours) String.format("%02d:%02d:%02d", HH, MM, SS) else String.format("%02d:%02d", MM, SS))
            ))
        }
        list.addAll(listOf(
            color("&d&m----------------------"),
            color("&7pressedmc.dev")
        ))

        board.updateLines(
            list
        )
    }

}