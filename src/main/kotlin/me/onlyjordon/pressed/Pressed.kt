package me.onlyjordon.pressed

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.kotlindsl.*
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import me.onlyjordon.pressed.commands.GamemodeCommands
import me.onlyjordon.pressed.commands.TeleportCommands
import me.onlyjordon.pressed.cosmetics.BlockType
import me.onlyjordon.pressed.cosmetics.CosmeticType
import me.onlyjordon.pressed.cosmetics.HelmetType
import me.onlyjordon.pressed.cosmetics.StickType
import me.onlyjordon.pressed.cosmetics.gui.CosmeticGui
import me.onlyjordon.pressed.events.Event
import me.onlyjordon.pressed.events.sumo.SumoEvent
import me.onlyjordon.pressed.globalboosters.BoosterDataManager
import me.onlyjordon.pressed.globalboosters.BoosterManager
import me.onlyjordon.pressed.listener.BlockListener
import me.onlyjordon.pressed.listener.KitEditorListener
import me.onlyjordon.pressed.listener.LifecycleListener
import me.onlyjordon.pressed.nick.NickListener
import me.onlyjordon.pressed.quests.BlocksPlacedQuest
import me.onlyjordon.pressed.quests.KillsQuest
import me.onlyjordon.pressed.quests.KillstreakQuest
import me.onlyjordon.pressed.quests.daily.DailyBlocksPlacedQuest
import me.onlyjordon.pressed.quests.daily.DailyKillsQuest
import me.onlyjordon.pressed.quests.daily.DailyKillstreakQuest
import me.onlyjordon.pressed.quests.gui.QuestGui
import me.onlyjordon.pressed.quests.gui.QuestGuiListener
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.tasks.GameLoop
import me.onlyjordon.pressed.util.BoardManager
import me.onlyjordon.pressed.util.papi.PressedExpansion
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.trait.SkinTrait
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class Pressed : JavaPlugin() {
    lateinit var world: World
        private set
    lateinit var kitEditorLocation: Location
        private set
    lateinit var cosmeticShopLocation: Location
        private set
    var spawnLocations: List<Location> = ArrayList()
        private set
    val randomSpawnLocation: Location
        get() = spawnLocations.random()
    var buildheight = 48
        private set
    var kitheight = 51
        private set
    lateinit var lifecycleListener: LifecycleListener
        private set
    lateinit var boardManager: BoardManager
        private set
    lateinit var kitEditorNPC: NPC
        private set
    lateinit var cosmeticShopNPC: NPC
        private set
    lateinit var killQuest: KillsQuest
        private set
    lateinit var killstreakQuest: KillstreakQuest
        private set
    lateinit var blocksPlacedQuest: BlocksPlacedQuest
        private set
    lateinit var dailyKillQuest: DailyKillsQuest
        private set
    lateinit var dailyKillstreakQuest: DailyKillstreakQuest
        private set
    lateinit var dailyBlocksPlacedQuest: DailyBlocksPlacedQuest
        private set
    lateinit var globalMultiConfigFile: File
        private set
    lateinit var globalMultiConfig: YamlConfiguration
        private set
    lateinit var boosterManager: BoosterManager
        private set
    lateinit var boosterDataManager: BoosterDataManager
        private set
    var currentGlobalBooster = 1.0

    var shopItemStick: StickType = CosmeticType.getRandomItemWithProbabilities(StickType::class.java)
    var shopItemHelmet: HelmetType = CosmeticType.getRandomItemWithProbabilities(HelmetType::class.java)
    var shopItemBlock: BlockType = CosmeticType.getRandomItemWithProbabilities(BlockType::class.java)

    fun regenerateShopItems() {
        shopItemStick = CosmeticType.getRandomItemWithProbabilities(StickType::class.java)
        shopItemHelmet = CosmeticType.getRandomItemWithProbabilities(HelmetType::class.java)
        shopItemBlock = CosmeticType.getRandomItemWithProbabilities(BlockType::class.java)
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).usePluginNamespace())
        CommandAPI.unregister("gamemode", false)
        CommandAPI.unregister("teleport", false)
        CommandAPI.unregister("tp", false)
        CommandAPI.unregister("ver", true)
        CommandAPI.unregister("version", true)
        CommandAPI.unregister("icanhasbukkit", true)

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
//        PacketEvents.getAPI().eventManager.registerListener(
//            NickListener(), PacketListenerPriority.NORMAL
//        )
        PacketEvents.getAPI().init()
        saveDefaultConfig()
        loadConfigValues()

        boardManager = BoardManager().setup()
        registerListeners()
        registerCommands()
        spawnNPCs()
        startTasks()

        PressedExpansion.registerHook()

        killQuest = KillsQuest()
        killstreakQuest = KillstreakQuest()
        blocksPlacedQuest = BlocksPlacedQuest()

        dailyKillQuest = DailyKillsQuest()
        dailyKillstreakQuest = DailyKillstreakQuest()
        dailyBlocksPlacedQuest = DailyBlocksPlacedQuest()

        Bukkit.getOnlinePlayers().forEach {
            lifecycleListener.onJoin(PlayerJoinEvent(it, ""))
            boardManager.onJoin(PlayerJoinEvent(it, ""))
        }
        globalMultiConfigFile = File(dataFolder, "global-multi.yml")
        globalMultiConfig = YamlConfiguration.loadConfiguration(globalMultiConfigFile)
        boosterManager = BoosterManager()
        boosterManager.startBoosterCleanupTask()
        boosterDataManager = BoosterDataManager()
        boosterDataManager.loadBoosters(boosterManager.activeBoosters)
        boosterManager.startBoosterCleanupTask()
        boosterManager.applyActiveBoosters()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            lifecycleListener.onQuit(PlayerQuitEvent(it, ""))
            boardManager.onQuit(PlayerQuitEvent(it, ""))
        }
        PressedExpansion.unregisterHook()
        kitEditorNPC.destroy()
        cosmeticShopNPC.destroy()
        boosterDataManager.saveBoosters(boosterManager.activeBoosters)
        PacketEvents.getAPI().terminate()
    }

    fun loadConfigValues() {
        buildheight = config.getInt("build-limit")
        kitheight = config.getInt("kit-give-height")
        spawnLocations = config.getList("spawns") as List<Location>
        kitEditorLocation = config.getLocation("kit-editor-location")!!
        cosmeticShopLocation = config.getLocation("cosmetic-shop-location")!!
        world = Bukkit.getWorld(config.getString("world")!!)!!
    }

    fun registerListeners() {
        lifecycleListener = LifecycleListener()
        Bukkit.getPluginManager().registerEvents(lifecycleListener, this)
        Bukkit.getPluginManager().registerEvents(BlockListener(), this)
        Bukkit.getPluginManager().registerEvents(KitEditorListener(), this)
        Bukkit.getPluginManager().registerEvents(QuestGuiListener(), this)
//        Bukkit.getPluginManager().registerEvents(NickListener(), this)

    }

    fun spawnNPCs() {
        kitEditorNPC = CitizensAPI.getTemporaryNPCRegistry().createNPC(EntityType.PLAYER, "&5Kit Editor")
        val st = SkinTrait()
        kitEditorNPC.addTrait(st)
        st.skinName = "onlyjordon"
        kitEditorNPC.spawn(kitEditorLocation)

        cosmeticShopNPC = CitizensAPI.getTemporaryNPCRegistry().createNPC(EntityType.PLAYER, "&dCosmetics")
        val st2 = SkinTrait()
        cosmeticShopNPC.addTrait(st2)
        st2.skinName = "Bitbanga"
        cosmeticShopNPC.spawn(cosmeticShopLocation)
    }

    var currentEvent: Event? = null

    fun registerCommands() {
        TeleportCommands.register()
        GamemodeCommands.register()
//        NickCommands.register()
        commandAPICommand("cosmetics") {
            aliases = arrayOf("mycosmetics", "cosmetic")
            playerExecutor { player, _ ->
                CosmeticGui(UserManager.getUser(player.uniqueId)).open()
            }
        }

        commandAPICommand("toggledeathmessages") {
            aliases = arrayOf("tdm", "deathmessages", "toggledm", "tdeathmessages")
            playerExecutor { player, _ ->
                val user = UserManager.getUser(player.uniqueId)
                user.deathMessagesEnabled = !user.deathMessagesEnabled
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fPlayer &7death messages&f are now &7${if (user.deathMessagesEnabled) "visible" else "hidden"}&f!"))
            }
        }

        commandAPICommand("event") {
            arguments(
                StringArgument("eventName").replaceSuggestions(ArgumentSuggestions.strings("sumo")),
                StringArgument("action").replaceSuggestions(ArgumentSuggestions.strings("join", "start", "quit", "leave"))
            )
            playerExecutor { player, args ->
                val eventName: String = args.get("eventName") as String
                val action: String = args.get("action") as String
                val u = UserManager.getUser(player.uniqueId)
                if (u.killer.second > System.currentTimeMillis()) {
                    player.sendMessage(ChatColor.RED.toString() + "You can't do this in combat!")
                    return@playerExecutor
                }
                if (eventName.equals("sumo", true)) {
                    if (action.equals("start", true) && player.hasPermission("pressed.events.start.sumo")) {
                        if (currentEvent?.hasEnded == false) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn event is already running!"))
                            return@playerExecutor
                        }
                        currentEvent = SumoEvent()
                        (currentEvent as SumoEvent).start()
                        Bukkit.getPluginManager().registerEvents(currentEvent as SumoEvent, this@Pressed)
                    }
                    if (action.equals("quit", true) || action.equals("leave", true)) {
                        (currentEvent as? SumoEvent)?.quit(player)
                    }
                    if (action.equals("join", true)) {
                        if (currentEvent?.hasEnded == true) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat event has ended!"))
                            return@playerExecutor
                        }
                        if (currentEvent?.hasStarted == true) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat event has already started!"))
                            return@playerExecutor
                        }
                        (currentEvent as? SumoEvent)?.join(player)
                        player.inventory.clear()
                        player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                    }
                }
            }
        }

        commandAPICommand("bc") {
            aliases = arrayOf("broadcast")
            permission = CommandPermission.fromString("pressed.broadcast.use")
            greedyStringArgument("message")
            anyExecutor { executor, args ->
                Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&5[&dBroadcast&5] &f" + args.get("message") as String))
            }
        }

        commandAPICommand("coins") {
            permission = CommandPermission.fromString("pressed.coins.use")
            stringArgument("action")
            playerArgument("player")
            optionalArguments(LongArgument("amount"))
            anyExecutor { executor, args ->
                val action = args.get("action") as String
                val other = args.get("player") as Player
                val amount = args.get("amount") as Long?
                if (action == "give" && amount != null) {
                    val user = UserManager.getUser(other.uniqueId)
                    user.coins += amount
                    if (user.coins <= 0) user.coins = 0
                }
                if (action == "reset") {
                    UserManager.getUser(other.uniqueId).coins = 0
                }

                if (action == "set" && amount != null) {
                    UserManager.getUser(other.uniqueId).coins = amount
                }
            }
        }


        commandAPICommand("rules") {
            anyExecutor { executor, _ ->
                executor.sendMessage(miniMessage().deserialize("<dark_purple>Server Rules:</dark_purple>"))
                executor.sendMessage(miniMessage().deserialize("<hover:show_text:'<aqua>Click to see discord server!'><click:run_command:'/discord'><dark_purple>1.<light_purple> No Cheating/abusing bugs, please report in <aqua>/discord</click></hover>"))
                executor.sendMessage(miniMessage().deserialize("<dark_purple>2.<light_purple> No Spamming/Chat Flooding"))
                executor.sendMessage(miniMessage().deserialize("<dark_purple>2.<light_purple> Keep swearing to a minimum"))
                executor.sendMessage(miniMessage().deserialize("<dark_purple>2.<light_purple> Use common sense please"))
            }
        }

        commandAPICommand("discord") {
            anyExecutor {executor, _ ->
                val comp = Component.text("Join the discord server! https://discord.gg/vbKW2YecAr").color(TextColor.color(ChatColor.LIGHT_PURPLE.color.rgb)).clickEvent(
                    ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/vbKW2YecAr")
                ).hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Click to join the discord server!").color(TextColor.color(ChatColor.LIGHT_PURPLE.color.rgb))))
                executor.sendMessage(comp)
            }
        }

        commandAPICommand("stats") {
            optionalArgument(OfflinePlayerArgument("other"))
            anyExecutor { executor, args ->
                val other = args.get("other") as? OfflinePlayer
                if (other == null) {
                    if (executor !is Player) {
                        executor.sendMessage("Please specify a player!")
                        return@anyExecutor
                    }
                    sendStats(executor, executor)
                } else {
                    sendStats(executor, other)
                }
            }
        }

        commandAPICommand("quests") {
//            argument(StringArgument("questType").replaceSuggestions(ArgumentSuggestions.strings("blocks-placed", "kills", "killstreak")))
            playerExecutor {player, args ->
                sendQuests(UserManager.getUser(player.uniqueId))
//                val quest = args.get("questType") as String
//                if (quest.equals("blocks-placed", true)) {
//                    sendQuests(player, blocksPlacedQuest)
//                }
//                if (quest.equals("kills", true)) {
//                    sendQuests(player, killQuest)
//                }
//                if (quest.equals("killstreak", true)) {
//                    sendQuests(player, killstreakQuest)
//                }
            }
        }

        commandAPICommand("gmulti") {
            permission = CommandPermission.fromString("pressed.globalmultipliers.activate")
            arguments(OfflinePlayerArgument("player"), DoubleArgument("amount"), StringArgument("time").replaceSuggestions(
                ArgumentSuggestions.strings { if (it.currentArg.matches("\\d+".toRegex())) arrayOf("${it.currentArg}d","${it.currentArg}mo","${it.currentArg}s") else arrayOf("") } ))
            anyExecutor { console, args ->
                val player = args.get("player") as OfflinePlayer
                val amount = args.get("amount") as Double
                val time = args.get("time") as String
                if (!time.matches("^\\d+(mo|[ds])\$".toRegex())) {
                    console.sendMessage("$time is an invalid time! Format: \$s, \$d, \$mo \n($ = amount, s = seconds, d = days, mo = months)")
                    return@anyExecutor
                }
                var actTime = 0L

                if (time.endsWith("s")) {
                    actTime = (time.substring(0, time.lastIndex).toLong() * 1000)
                }
                if (time.endsWith("d")) {
                    actTime = (time.substring(0, time.lastIndex).toLong() * 86400 * 1000)
                }
                if (time.endsWith("mo")) {
                    actTime = (time.substring(0, time.lastIndex-1).toLong() * 86400 * 1000 * 28)
                }

                console.sendMessage("Activating ${amount}x global multiplier for ${player.name ?: "Unknown Player"} for $time!")
                Bukkit.getServer().broadcast(miniMessage().deserialize("<gold>Thank you <light_purple>${player.name ?: "Unknown Player"}</light_purple> for using a <dark_purple>${amount}x</dark_purple> global multiplier for <dark_purple>$time</dark_purple></gold>"))
                boosterManager.applyBooster(player.uniqueId, amount, actTime)
                boosterDataManager.saveBoosters(boosterManager.activeBoosters)
            }
        }
    }


    fun sendQuests(user: me.onlyjordon.pressed.stats.User) {
        QuestGui(user).open()
//        player.sendMessage(miniMessage().deserialize("<dark_purple>Quest ${quest.name}</dark_purple>"))
    }

    fun sendStats(receiver: CommandSender, other: OfflinePlayer) {
        if ((!other.isOnline && !other.hasPlayedBefore()) || other.name == null) {
            receiver.sendMessage("That player has never played before!")
            return
        }
        val user = UserManager.getUser(other.uniqueId)
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Stats for <dark_purple>${other.name}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Kills: <gray>${user.kills}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Deaths: <gray>${user.deaths}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Killstreak: <gray>${user.killstreak}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Highest Killstreak: <gray>${user.highestKillstreak}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>Coins: <gray>${user.coins}"))
        receiver.sendMessage(miniMessage().deserialize("<light_purple>XP: <gray>${user.xp}"))
    }

    fun startTasks() {
        GameLoop().runTaskTimer(this, 5L, 5L)
    }
}
