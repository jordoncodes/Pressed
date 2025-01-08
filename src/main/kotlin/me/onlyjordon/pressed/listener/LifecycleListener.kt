package me.onlyjordon.pressed.listener

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.HashMultimap
import me.onlyjordon.pressed.cosmetics.shop.CosmeticShopGui
import me.onlyjordon.pressed.fakerank.FakeRankManager
import me.onlyjordon.pressed.kiteditor.KitEditorGui
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.BlockManager
import me.onlyjordon.pressed.util.UsefulFunctions.isInArena
import me.onlyjordon.pressed.util.UsefulFunctions.isInArenaWorld
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.npc.NPC
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EnderPearl
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap

class LifecycleListener: Listener {

    val map: Cache<UUID, Long> = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(5)).build<UUID, Long>()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage = ChatColor.translateAlternateColorCodes('&', "&5[&a+&5] &d${player.name} joined the game")
        FakeRankManager.resetFakeRank(player)
        player.teleport(plugin.randomSpawnLocation)
        val user = UserManager.getUser(player.uniqueId)
        user.respawn()
    }

    @EventHandler
    fun preJoin(event: AsyncPlayerPreLoginEvent) {
        if (map.getIfPresent(event.uniqueId) != null) {
            event.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
            event.kickMessage(miniMessage().deserialize("<red>You are joining quickly! Please retry in 5 seconds!</red>"))
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (!player.isInArenaWorld) return
        val user = UserManager.getUser(player.uniqueId)
        if (player.gameMode == GameMode.CREATIVE) return
        if (!isInArena(event.from) && isInArena(event.to)) {
            user.giveKit()
        }
        if (isInArena(event.from) && !isInArena(event.to)) {
            user.respawn()
        }
        if (player.y < -45) {
            player.damage(10000.0)
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (!event.to.isInArenaWorld) return
        val user = UserManager.getUser(player.uniqueId)
        if (player.gameMode == GameMode.CREATIVE) return
        if (!isInArena(event.from) && isInArena(event.to)) {
            user.giveKit()
        }
        if (isInArena(event.from) && !isInArena(event.to)) {
            if (event.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                event.isCancelled = true
                return
            }
            user.respawn(teleport = false)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        BlockManager.removeAll(player.uniqueId)
        FakeRankManager.resetFakeRank(player)
        val user = UserManager.getUser(player.uniqueId)
        if (user.killer.second > System.currentTimeMillis()) {
            user.killer.first?.let {
                user.kill(it)
                user.killer = null to System.currentTimeMillis()
                player.killer = null
                plugin.killQuest.checkRequirementAndComplete(it)
                plugin.killstreakQuest.checkRequirementAndComplete(it)
                plugin.dailyKillQuest.checkRequirementAndComplete(it)
                plugin.dailyKillstreakQuest.checkRequirementAndComplete(it)
                plugin.killQuest.checkRequirementAndComplete(player)
                plugin.killstreakQuest.checkRequirementAndComplete(player)
                plugin.dailyKillQuest.checkRequirementAndComplete(player)
                plugin.dailyKillstreakQuest.checkRequirementAndComplete(player)
                Bukkit.getOnlinePlayers().forEach { p ->
                    val u1 = UserManager.getUser(p.uniqueId)
                    if (u1.deathMessagesEnabled) {
                        if (p == player) {
                            p.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&5You &dwere ${String.format(killMessages.random(), it.name)}&d."
                                )
                            )
                            return@forEach
                        }
                        if (p == it) {
                            p.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&5${player.name} &dwas ${
                                        String.format(
                                            killMessages.random(),
                                            "you"
                                        )
                                    }&d. &f[&5+${(10 * u1.killMultiplier).toInt()} xp &7/&5 +${(10 * u1.killMultiplier).toInt()} coins&f]&d."
                                )
                            )
                            return@forEach
                        }
                        p.sendMessage(
                            ChatColor.translateAlternateColorCodes(
                                '&',
                                "&5${player.name} &dwas ${String.format(killMessages.random(), it.name)}&d."
                            )
                        )
                    }
                }
            }
        }
        user.save()
        UserManager.clear(user)
        event.quitMessage = ChatColor.translateAlternateColorCodes('&', "&5[&c-&5] &d${player.name} left the game")
        map.put(player.uniqueId, 0L)
    }


    @EventHandler
    fun onKick(event: PlayerKickEvent) {
        onQuit(PlayerQuitEvent(event.player, ""))
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        Bukkit.getServer().onlinePlayers.forEach {
            UserManager.getUser(it.uniqueId).save()
        }
        UserManager.userMap.values.filter { !it.player.isOnline }.forEach {
            it.save()
            UserManager.clear(it)
        }
    }

    @EventHandler
    fun onHungerLoss(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onNPCClick(event: NPCRightClickEvent) {
        npcClick(event.npc, event.clicker)
    }

    @EventHandler
    fun onNPCClick(event: NPCLeftClickEvent) {
        npcClick(event.npc, event.clicker)
    }

    fun npcClick(npc: NPC, clicker: Player) {
        if (npc == plugin.kitEditorNPC) {
            KitEditorGui(UserManager.getUser(clicker.uniqueId)).open()
        }
        if (npc == plugin.cosmeticShopNPC) {
            CosmeticShopGui(UserManager.getUser(clicker.uniqueId)).open()
        }
    }

    val killMessages = listOf("brutally murdered by &5%s", "killed by &5%s", "dipped into the void by &5%s", "dead to &5%s", "dropped off the map by &5%s", "sent to spawn by &5%s", "murdered by &5%s", "erased by &5%s", "eaten by &5%s", "cooked by &5%s")

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (event.player.gameMode != GameMode.CREATIVE) event.isCancelled = true
    }


    var playerPearls: HashMultimap<UUID, EnderPearl> = HashMultimap.create()

    @EventHandler
    fun onPearlThrow(event: ProjectileLaunchEvent) {
        if (event.entityType == EntityType.ENDER_PEARL) {
            playerPearls[event.entity.ownerUniqueId!!].add(event.entity as EnderPearl)
        }
    }

    @EventHandler
    fun onOffhandSwap(event: PlayerSwapHandItemsEvent) {
        if (isInArena(event.player.location)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (isInArena(event.whoClicked.location) && event.whoClicked.gameMode != GameMode.CREATIVE) {
            event.isCancelled = true
//            event.whoClicked.sendMessage(event.rawSlot.toString() + " / " + event.slot.toString())
        }
    }



    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val player = event.entity
        if (player !is Player) return
        if (!player.isInArenaWorld) return
        if (player.health - event.finalDamage <= 0) {
            player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
            player.fireTicks = 0
            player.isVisualFire = false
            player.saturation = 20f
            player.foodLevel = 20
            val user = UserManager.getUser(player.uniqueId)
            user.respawn()
            event.isCancelled = true
            playerPearls.get(player.uniqueId).forEach {
                it.remove()
            }
            playerPearls.removeAll(player.uniqueId)
            user.killer.first?.let {
                if (it == player) return
                if (user.killer.second < System.currentTimeMillis()) {
                    user.kill(null)
                    return
                }
                user.kill(it)
                user.killer = null to System.currentTimeMillis()
                player.killer = null
                plugin.killQuest.checkRequirementAndComplete(it)
                plugin.killstreakQuest.checkRequirementAndComplete(it)
                plugin.dailyKillQuest.checkRequirementAndComplete(it)
                plugin.dailyKillstreakQuest.checkRequirementAndComplete(it)
                plugin.killQuest.checkRequirementAndComplete(player)
                plugin.killstreakQuest.checkRequirementAndComplete(player)
                plugin.dailyKillQuest.checkRequirementAndComplete(player)
                plugin.dailyKillstreakQuest.checkRequirementAndComplete(player)
                Bukkit.getOnlinePlayers().forEach { p ->
                    val u1 = UserManager.getUser(p.uniqueId)
                    if (u1.deathMessagesEnabled) {
                        if (p == player) {
                            p.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&5You &dwere ${String.format(killMessages.random(), it.name)}&d."
                                )
                            )
                            return@forEach
                        }
                        if (p == it) {
                            p.sendMessage(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    "&5${player.name} &dwas ${
                                        String.format(
                                            killMessages.random(),
                                            "you"
                                        )
                                    }&d. &f[&5+${(10 * u1.killMultiplier).toInt()} xp &7/&5 +${(10 * u1.killMultiplier).toInt()} coins&f]&d."
                                )
                            )
                            return@forEach
                        }
                        p.sendMessage(
                            ChatColor.translateAlternateColorCodes(
                                '&',
                                "&5${player.name} &dwas ${String.format(killMessages.random(), it.name)}&d."
                            )
                        )
                    }
                }
            } ?: {
                user.kill(null)
            }
            player.setItemOnCursor(ItemStack(Material.AIR, 1))
        }
    }

    @EventHandler
    fun onDamageByPlayer(event: EntityDamageByEntityEvent) {
        val player = event.entity
        if (player !is Player) return
        if (!player.isInArenaWorld) return
        val damager = event.damager
        if (damager !is Player) return
        if (!(isInArena(damager.location) && isInArena(player.location))) {
            event.isCancelled = true
            return

        }
        event.damage = 0.0000000001
        player.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        val user = UserManager.getUser(player.uniqueId)
        user.killer = damager to System.currentTimeMillis() + 60000L
        val user2 = UserManager.getUser(damager.uniqueId)
        user2.killer = user2.killer.first to System.currentTimeMillis() + 60000L
    }
}