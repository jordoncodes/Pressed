package me.onlyjordon.pressed.koth

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class KothArea(
    val cuboidRegion: CuboidRegion,
    val rewardCoinsPerSecond: Int = 6,
    val captureTimeSeconds: Int = 15
) {
    var currentOwner: Player? = null
    var captureProgress: MutableMap<Player, Int> = mutableMapOf()
    var isActive: Boolean = false

    fun isInside(player: Player): Boolean {
        if (!isActive) return false
        val loc: Location = player.location
        return cuboidRegion.contains(
            BlockVector3.at(loc.blockX, loc.blockY, loc.blockZ)
        )
    }

    fun processPlayer(player: Player) {
        if (!isActive) return
        if (!isInside(player)) {
            captureProgress.remove(player)
            return
        }
        if (currentOwner == player) return
        captureProgress.putIfAbsent(player, 0)

        if (captureProgress.size > 1) {
            return
        }

        val currentProgress = captureProgress.getOrDefault(player, 0) + 1

        if (currentProgress >= captureTimeSeconds) {

            if (currentOwner != player) {
                currentOwner = player
                captureProgress.remove(player)
                Bukkit.broadcastMessage("${player.name} has captured the KOTH platform!")
            }
        } else {
            captureProgress[player] = currentProgress
        }
    }

    fun tick() {
        isActive = Bukkit.getOnlinePlayers().size > 3
        if (!isActive) return
        currentOwner?.let { owner ->
            rewardPlayer(owner)
        }
        Bukkit.getOnlinePlayers().forEach { player ->
            processPlayer(player)
        }
    }

    private fun rewardPlayer(player: Player) {
        if (!isActive) return
        val user = UserManager.getUser(player.uniqueId)
        user.coins+=(rewardCoinsPerSecond * UsefulFunctions.plugin.currentGlobalBooster).toLong()
    }
}