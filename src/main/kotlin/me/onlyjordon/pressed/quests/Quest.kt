package me.onlyjordon.pressed.quests

import com.google.common.collect.HashMultimap
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.math.log
import kotlin.math.roundToLong

open abstract class Quest {
    abstract val levels: List<Int>
    val completeLevels: HashMultimap<UUID, Int> = HashMultimap.create()
    abstract val name: String
    open val isDaily = false

    lateinit var file: File
    lateinit var config: YamlConfiguration

    fun setup() {
        file = File(plugin.dataFolder, "quest-$name.yml")
        if (file.parentFile?.exists() != true) file.parentFile?.mkdirs()
        if (!file.exists()) file.createNewFile()
        config = YamlConfiguration.loadConfiguration(file)
    }

    abstract fun checkRequirement(player: Player): Int

    fun complete(player: Player, indexOfLevel: Int): Boolean {
        val user = UserManager.getUser(player.uniqueId)
        if (completeLevels.get(player.uniqueId).isEmpty()) {
            load(player)
        }
        if (completeLevels.containsEntry(player.uniqueId, levels[indexOfLevel])) return false
        completeLevels.put(player.uniqueId, levels[indexOfLevel])
        val amt = (log(indexOfLevel.toDouble() + 2.0, 5.0) * 5).roundToLong()
        user.coins+=amt
        user.xp+=amt
        player.sendMessage(miniMessage().deserialize("<green>You have completed the <red>${levels[indexOfLevel]} $name</red> quest!</green> <yellow>+$amt Coins</yellow> <gray>/</gray> <light_purple>+$amt XP</light_purple>"))
        return true
    }

    fun checkRequirementAndComplete(player: Player) {
        while (true) {
            var newLevel = checkRequirement(player)
            if (newLevel < 0) break
            if (!complete(player, newLevel)) return
        }
    }

    fun unload(player: Player) {
        if (completeLevels.get(player.uniqueId).isEmpty()) return
        val lvls = completeLevels.get(player.uniqueId)
        config.set("completed.${player.uniqueId}", lvls.toMutableList())
        completeLevels.removeAll(player.uniqueId)
        config.save(file)
    }

    fun load(player: Player) {
        if (config.contains("completed.${player.uniqueId}")) {
            completeLevels.putAll(player.uniqueId, config.getIntegerList("completed.${player.uniqueId}"))
        }
    }
}