package me.onlyjordon.pressed.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.mojang.authlib.GameProfile
import me.neznamy.tab.api.TabAPI
import me.onlyjordon.pressed.nick.NickListener
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.kyori.adventure.text.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.PlayerList
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.portal.DimensionTransition
import org.bukkit.Bukkit
import org.bukkit.World.Environment
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.EnumSet

object NickNamer {

    private val playersByName = PlayerList::class.java.getDeclaredField("playersByName")
    val realNames = HashMap<String, String>()
    init {
        playersByName.isAccessible = true
    }

    val Player.realName: String
        get() = realNames[name] ?: name

    fun getPlayersByName(): HashMap<String, ServerPlayer> {
        val playerList = (Bukkit.getServer() as CraftServer).handle.server.playerList
        return playersByName.get(playerList) as HashMap<String, ServerPlayer>
    }

    fun realSetPlayerName(player: Player, name: String) {
        val players = getPlayersByName()
        players.remove(player.name)
        players.remove(NickListener.nameMap[player.uniqueId] ?: player.name)
        var realName = player.name
        if (realNames[player.name] != null) {
            realName = realNames[player.name]!!
            realNames.remove(player.name)
        }
        realNames[name] = realName
        players[name] = (player as CraftPlayer).handle
        val oldProfile = player.handle.gameProfile
        val newProfile = GameProfile(oldProfile.id, name)
        newProfile.properties.putAll(oldProfile.properties)
        player.handle.gameProfile = newProfile
    }

    fun setNickname(player: Player, name: String, alsoSkin: Boolean = true) {
        NickListener.nameMap[player.uniqueId] = name
        if (alsoSkin)
            NickListener.skinMap[player.uniqueId] = SkinFetcher.fetchSkin(name)
        if (alsoSkin)
            refreshForSelf(player)
        realSetPlayerName(player, NickListener.nameMap[player.uniqueId]!!)
        refreshForOthers(player)
    }

    fun setSkin(player: Player, name: String) {
        NickListener.skinMap[player.uniqueId] = SkinFetcher.fetchSkin(name)
        refreshForOthers(player)
        refreshForSelf(player)
    }

    fun setSkin(player: Player, textureProperties: MutableList<TextureProperty>) {
        NickListener.skinMap[player.uniqueId] = textureProperties
        refreshForOthers(player)
        refreshForSelf(player)
    }

    fun reset(player: Player) {
        NickListener.nameMap.remove(player.uniqueId)
        NickListener.skinMap.remove(player.uniqueId)
        realSetPlayerName(player, player.realName)
        refreshForOthers(player)
        refreshForSelf(player)
    }

    fun refreshForSelf(player: Player) {
        val p1 = WrapperPlayServerPlayerInfoRemove(listOf(player.uniqueId))
        val p2 = WrapperPlayServerPlayerInfoUpdate(EnumSet.allOf(WrapperPlayServerPlayerInfoUpdate.Action::class.java),
            WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                UserProfile(player.uniqueId, player.name, NickListener.skinMap[player.uniqueId] ?: PacketEvents.getAPI().playerManager.getUser(player).profile.textureProperties),
                true,
                0,
                GameMode.getById(player.gameMode.ordinal),
                null,
                null
            )
        )
        PacketEvents.getAPI().playerManager.sendPacket(player, p1)
        PacketEvents.getAPI().playerManager.sendPacket(player, p2)
        if (PacketEvents.getAPI().playerManager.getUser(player).clientVersion.isOlderThan(ClientVersion.V_1_17)) {
            // calls PlayerRespawnEvent ;-;
            (player.world as CraftWorld).handle.server.playerList.respawn((player as CraftPlayer).handle, true, Entity.RemovalReason.CHANGED_DIMENSION, PlayerRespawnEvent.RespawnReason.PLUGIN, player.location)
            player.teleport(player)
        } else {
            val m = CraftPlayer::class.java.getDeclaredMethod("refreshPlayer")
            m.isAccessible = true
            m.invoke(player)
        }
    }


    fun refreshForOthers(player: Player) {

        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(player)
            it.showPlayer(player)
        }
        val tab = TabAPI.getInstance()
        val tabPlayer = tab.getPlayer(player.uniqueId)!!
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            tab.nameTagManager!!.pauseTeamHandling(tabPlayer)
            tab.nameTagManager!!.resumeTeamHandling(tabPlayer)
            tab.nameTagManager!!.setPrefix(tabPlayer, tab.nameTagManager!!.getCustomPrefix(tabPlayer))
        }, 5L)
    }
}