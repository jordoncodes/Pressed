package me.onlyjordon.pressed.stats

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI
import gg.auroramc.levels.api.AuroraLevelsProvider
import me.onlyjordon.pressed.Pressed
import me.onlyjordon.pressed.cosmetics.BlockType
import me.onlyjordon.pressed.cosmetics.HelmetType
import me.onlyjordon.pressed.cosmetics.StickType
import me.onlyjordon.pressed.util.BlockManager
import me.onlyjordon.pressed.util.StackBuilder
import me.onlyjordon.pressed.util.UsefulFunctions
import me.onlyjordon.pressed.util.UsefulFunctions.isInArena
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.Calendar
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec
import kotlin.math.ln
import kotlin.math.roundToLong

class User(val player: OfflinePlayer) {

    var deathMessagesEnabled: Boolean = true
    var killer: Pair<Player?,Long> = (null to System.currentTimeMillis())
    var killMultiplier = Math.E
    var sumoEventWins: Int = 0
    var tntRunEventWins: Int = 0
    var coins = 0L
    var highestKillstreak: Int = 0
    var killstreak: Int = 0
    var kills: Int = 0
    var deaths: Int = 0
    var xp: Long = 0
    var blocksPlaced = 0
    var blazeDust = 0L

    var dailyKillstreak = 0
    var dailyKills = 0
    var dailyBlocksPlaced = 0

    var day = 0

    val plugin = JavaPlugin.getPlugin(Pressed::class.java)

    var stick: StickType = StickType.DEFAULT
    var ownedSticks: ArrayList<StickType> = ArrayList()
    var stickSlot = 0

    var helmet: HelmetType = HelmetType.DEFAULT
    var ownedHelmets: ArrayList<HelmetType> = ArrayList()

    var blockType: BlockType = BlockType.DEFAULT
    var ownedBlocks: ArrayList<BlockType> = ArrayList()
    var blockSlot = 1

    var pearlSlot = 2
    var webSlot = 3

    private var playerData: FileConfiguration
    private val playerDataFile = File(JavaPlugin.getProvidingPlugin(User::class.java).dataFolder, "${player.uniqueId}${File.separator}${player.uniqueId}.yml")

    init {
        playerData = YamlConfiguration.loadConfiguration(playerDataFile)
        load()
    }

    private fun load() {
        stick = StickType.valueOf(playerData.getString("stickCosmetic") ?: StickType.DEFAULT.name)
        helmet = HelmetType.valueOf(playerData.getString("helmetCosmetic") ?: StickType.DEFAULT.name)
        blockType = BlockType.valueOf(playerData.getString("blockCosmetic") ?: StickType.DEFAULT.name)

        ownedSticks = ArrayList(playerData.getStringList("ownedStickCosmetics").map { try {StickType.valueOf(it)} catch (e: Exception) {return} })
        ownedHelmets = ArrayList(playerData.getStringList("ownedHelmetCosmetics").map { try {HelmetType.valueOf(it)} catch (e: Exception) {return} })
        ownedBlocks = ArrayList(playerData.getStringList("ownedBlockCosmetics").map { try {BlockType.valueOf(it)} catch (e: Exception) {return} })

        if (!ownedSticks.contains(StickType.DEFAULT)) ownedSticks.add(StickType.DEFAULT)
        if (!ownedHelmets.contains(HelmetType.DEFAULT)) ownedHelmets.add(HelmetType.DEFAULT)
        if (!ownedBlocks.contains(BlockType.DEFAULT)) ownedBlocks.add(BlockType.DEFAULT)
        if (!ownedSticks.contains(StickType.CRYING)) ownedSticks.add(StickType.CRYING)

        highestKillstreak = playerData.getInt("highestKillstreak")
        kills = playerData.getInt("kills")
        deaths = playerData.getInt("deaths")
        xp = playerData.getLong("xp")
        coins = playerData.getLong("coins")
        blazeDust = playerData.getLong("blaze-dust")
        sumoEventWins = playerData.getInt("sumo-event-wins")
        tntRunEventWins = playerData.getInt("tnt-run-event-wins")
        blocksPlaced = playerData.getInt("blocks-placed")
            if (playerData.contains("stickSlot"))
            stickSlot = playerData.getInt("stickSlot")
        if (playerData.contains("blockSlot"))
            blockSlot = playerData.getInt("blockSlot")
        if (playerData.contains("pearlSlot"))
            pearlSlot = playerData.getInt("pearlSlot")
        if (playerData.contains("webSlot"))
            webSlot = playerData.getInt("webSlot")

        if (playerData.contains("death-messages"))
            deathMessagesEnabled = playerData.getBoolean("death-messages")


        player.player?.let { player ->
            plugin.killQuest.load(player)
            plugin.blocksPlacedQuest.load(player)
            plugin.killstreakQuest.load(player)
            plugin.dailyKillQuest.load(player)
            plugin.dailyBlocksPlacedQuest.load(player)
            plugin.dailyKillstreakQuest.load(player)
        }
    }

    fun save() {
        playerData.set("stickCosmetic", stick.name)
        playerData.set("helmetCosmetic", helmet.name)
        playerData.set("blockCosmetic", blockType.name)
        playerData.set("highestKillstreak", highestKillstreak)
        playerData.set("kills", kills)
        playerData.set("deaths", deaths)
        playerData.set("xp", xp)
        playerData.set("coins", coins)
        playerData.set("blaze-dust", blazeDust)
        playerData.set("blocks-placed", blocksPlaced)
        playerData.set("sumo-event-wins", sumoEventWins)
        playerData.set("tnt-run-event-wins", tntRunEventWins)
        playerData.set("stickSlot", stickSlot)
        playerData.set("blockSlot", blockSlot)
        playerData.set("pearlSlot", pearlSlot)
        playerData.set("webSlot", webSlot)
        playerData.set("ownedStickCosmetics", ownedSticks.map { it.name })
        playerData.set("ownedHelmetCosmetics", ownedHelmets.map { it.name })
        playerData.set("ownedBlockCosmetics", ownedBlocks.map { it.name })
        playerData.set("death-messages", deathMessagesEnabled)
        playerData.save(playerDataFile)
        player.player?.let { player ->
            plugin.killQuest.unload(player)
            plugin.blocksPlacedQuest.unload(player)
            plugin.killstreakQuest.unload(player)
            plugin.dailyKillQuest.unload(player)
            plugin.dailyBlocksPlacedQuest.unload(player)
            plugin.dailyKillstreakQuest.unload(player)
        }
    }

    fun giveKit(): Boolean {
        if (player.isOnline) {

            if (((player as Player).inventory.getItem(stickSlot)?.type ?: Material.AIR) == Material.AIR) player.inventory.setItem(stickSlot, stick.stack)
            if ((player.inventory.getItem(blockSlot)?.type ?: Material.AIR) == Material.AIR) player.inventory.setItem(blockSlot, blockType.stack)
            if ((player.inventory.getItem(pearlSlot)?.type ?: Material.AIR) == Material.AIR)  player.inventory.setItem(pearlSlot, StackBuilder(Material.ENDER_PEARL).maxStackSize(99).named(miniMessage().deserialize("<aqua>ENDER PEARL</aqua>")).build())
            if ((player.inventory.getItem(webSlot)?.type ?: Material.AIR) == Material.AIR)  player.inventory.setItem(webSlot, StackBuilder(Material.COBWEB).maxStackSize(99).named(miniMessage().deserialize("<white>COBWEB</white>")).build())
            player.inventory.armorContents = arrayOf(null, null, null, helmet.stack)
            return true
        }
        val oldDay = day
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        if (oldDay != day) {
            dailyKills = 0
            dailyKillstreak = 0
            dailyBlocksPlaced = 0
        }
        return false
    }

    fun respawn(teleport: Boolean = true) {
        if (player.isOnline) {
            if (teleport) {
                val loc = plugin.randomSpawnLocation
                (player as Player).teleport(loc)
                player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key("minecraft", "entity.player.teleport"), Source.PLAYER, 1f, 1f))
            }
            (player as Player).inventory.clear()
            player.inventory.setItemInOffHand(ItemStack(Material.AIR))
//            BlockManager.removeAll(player.uniqueId)
        }
    }

    fun giveKillStats() {
        val pearl = StackBuilder(Material.ENDER_PEARL).maxStackSize(99).named(miniMessage().deserialize("<aqua>ENDER PEARL</aqua>")).build()
        val web = StackBuilder(Material.COBWEB).maxStackSize(99).named(miniMessage().deserialize("<white>COBWEB</white>")).build()
        kills++
        dailyKills++
        xp+=(10 * (ln(killMultiplier)*plugin.currentGlobalBooster)).toInt()
        coins += (10 * (ln(killMultiplier)*plugin.currentGlobalBooster)).toInt()
        killstreak++
        dailyKillstreak++
        if (killstreak % 2 == 0) {
            killMultiplier+=0.2
        }
        if (killstreak % 5 == 0) {
            if (player.isOnline) {
                if (!(player as Player).inventory.containsAtLeast(pearl, 99)) {
                    player.inventory.addItem(pearl)
                }
            }
        }
        if (killstreak > 10) {
            if (player.isOnline) {
                if (!(player as Player).inventory.containsAtLeast(pearl, 99)) {
                    player.inventory.addItem(pearl)
                }
            }
        }
        if (killstreak > 15) {
            if (player.isOnline) {
                if (!(player as Player).inventory.containsAtLeast(web, 99)) {
                    player.inventory.addItem(web)
                }
            }
        }
        if (killstreak % 10 == 0) {
            if (player.isOnline) {
                if (!(player as Player).inventory.containsAtLeast(web, 99)) {
                    player.inventory.addItem(web)
                }
            }
        }

        // 50% chance
        if ((0..100).random() >= 99) {
            blazeDust+=plugin.currentGlobalBooster.roundToLong()
            if (player.isOnline) {
                (player as Player).sendMessage(
                    miniMessage().deserialize(
                        "<light_purple>You received <gray>${
                            if (plugin.currentGlobalBooster == 1.0) "a" else plugin.currentGlobalBooster.roundToLong().toString() + "x"
                        } blaze dust</gray> from a kill!"
                    ))
            }
        }
        highestKillstreak = highestKillstreak.coerceAtLeast(killstreak)

        if (!player.isOnline) {
            save()
            UserManager.clear(this)
            return
        }
//        if ((0..200).random() == 200) {
//            GadgetsMenuAPI.getPlayerManager(player.player).addMysteryDust((50..2000).random() / 100)
//        }
//        AuroraLevelsProvider.getLeveler().addXpToPlayer(player.player, ((5..100).random() / 100).toDouble())
    }

    fun kill(killer: Player?) {
        val killerUser: User? = killer?.uniqueId?.let {
            UserManager.getUser(it)
        }
        killerUser?.player?.location?.let {
            if (isInArena(it)) killerUser.giveKit()
        }
        killerUser?.giveKillStats()
        deaths++
        killstreak = 0
        dailyKillstreak = 0
        killMultiplier = Math.E
        if (player.isOnline) {
            (player as Player).clearActivePotionEffects()
        }
        this.killer = null to System.currentTimeMillis()

    }
}