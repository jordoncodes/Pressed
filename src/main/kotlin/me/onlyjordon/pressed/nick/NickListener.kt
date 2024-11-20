package me.onlyjordon.pressed.nick

import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import me.onlyjordon.pressed.util.NickNamer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.ArrayList

// sorry to whoever reads this class
class NickListener: com.github.retrooper.packetevents.event.PacketListener,Listener {

    companion object {
        val playerMap = HashMap<UUID, UUID>()
        val nameMap = HashMap<UUID, String>()
        val skinMap = HashMap<UUID, MutableList<com.github.retrooper.packetevents.protocol.player.TextureProperty>>()
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        nameMap.remove(event.player.uniqueId)
    }

    override fun onPacketSend(event: PacketSendEvent?) {
        if (event?.packetType == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            val packet = WrapperPlayServerPlayerInfoUpdate(event)
            packet.entries.forEach {playerInfo ->
                val prof = playerInfo.gameProfile
                val realUUID = prof.uuid
                if (Bukkit.getPlayer(prof.uuid) == null) return@forEach
                val id = playerMap.getOrDefault(prof.uuid, UUID.randomUUID())
                playerMap[prof.uuid] = id
                Bukkit.getPlayer(id)?.let { nameMap.putIfAbsent(prof.uuid, it.name) }
                if ((event.getPlayer() as Player?)?.uniqueId != prof.uuid) {
                    prof.name = nameMap[prof.uuid] ?: Bukkit.getPlayer(prof.uuid)?.name ?: "Unknown Player"
                    prof.uuid = id
                    playerInfo.gameProfile = prof
                    playerInfo.latency = 0
                }
                val skin = skinMap[realUUID]
                if (skin != null) {
                    prof.textureProperties.clear()
                    prof.textureProperties.addAll(skin)
                }
            }
            event.markForReEncode(true)
        }

        if (event?.packetType == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.PLAYER_INFO_REMOVE) {
            val packet = WrapperPlayServerPlayerInfoRemove(event)
            val newMap = ArrayList<UUID>()
            val ids = ArrayList<UUID>()
            packet.profileIds.forEach {
                if (Bukkit.getPlayer(it) == null) {
                    newMap.add(playerMap.getOrDefault(it, it))
                    ids.add(it)
                    return@forEach
                }
                val id = playerMap.getOrDefault(it, it)
                newMap.add(if ((event.getPlayer() as Player).uniqueId == it) it else id)
            }
            packet.profileIds = newMap
            event.markForReEncode(true)
        }

        if (event?.packetType == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.SPAWN_ENTITY) {
            val packet = WrapperPlayServerSpawnEntity(event)
            if (packet.entityType != EntityTypes.PLAYER) return
            packet.uuid.ifPresent {
                if (Bukkit.getPlayer(it) == null) return@ifPresent
                val id = playerMap.getOrDefault(it, UUID.randomUUID())
                playerMap[it] = id
                packet.uuid = Optional.of(id)
            }
            event.markForReEncode(true)
        }

        if (event?.packetType == com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server.TEAMS) {
            val packet = WrapperPlayServerTeams(event)
            val newPlayers = ArrayList<String>()
            packet.players.forEach { playerName ->
                val realUUID = Bukkit.getPlayer(playerName)?.uniqueId
                if (realUUID != null) {
                    nameMap.putIfAbsent(realUUID, Bukkit.getPlayer(playerName)!!.name)
                    newPlayers.add(nameMap[realUUID]!!)
                } else if (NickNamer.realNames.values.contains(playerName)) {
                    NickNamer.realNames.forEach { if (it.value == playerName) newPlayers.add(it.key) }
                } else {
                    newPlayers.add(playerName)
                }
            }
            packet.players = newPlayers
            event.markForReEncode(true)
        }
    }
}