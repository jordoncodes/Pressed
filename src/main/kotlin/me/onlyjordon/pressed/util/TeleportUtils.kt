package me.onlyjordon.pressed.util

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.RelativeMovement
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.util.*

object TeleportUtils {
    private val TELEPORT_FLAGS: Set<RelativeMovement> = Collections.unmodifiableSet(
        setOf(
            RelativeMovement.X_ROT, RelativeMovement.Y_ROT
        )
    )

    private var justTeleportedField: Field? = null
    private var teleportPosField: Field? = null
    private var lastPosXField: Field? = null
    private var lastPosYField: Field? = null
    private var lastPosZField: Field? = null
    private var teleportAwaitField: Field? = null
    private var awaitingTeleportTimeField: Field? = null

    init {
        try {
            justTeleportedField = getField("justTeleported")
            teleportPosField = getField("awaitingPositionFromClient")
            lastPosXField = getField("lastPosX")
            lastPosYField = getField("lastPosY")
            lastPosZField = getField("lastPosZ")
            teleportAwaitField = getField("awaitingTeleport")
            awaitingTeleportTimeField = getField("awaitingTeleportTime")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    @Throws(NoSuchFieldException::class)
    private fun getField(name: String): Field {
        val field = ServerGamePacketListenerImpl::class.java.getDeclaredField(name)
        field.isAccessible = true
        return field
    }

    fun teleport(player: Player, location: Location) {
        val x: Double = location.getX()
        val y: Double = location.getY()
        val z: Double = location.getZ()
        val handle = (player as CraftPlayer).handle
        if (handle.containerMenu !== handle.inventoryMenu) handle.closeContainer()
        handle.setPos(x, y, z)
        val connection = handle.connection
        var teleportAwait = 40
        try {
            justTeleportedField!!.set(connection, true)
            teleportPosField!!.set(connection, Vec3(x, y, z))
            lastPosXField!!.set(connection, x)
            lastPosYField!!.set(connection, y)
            lastPosZField!!.set(connection, z)
            teleportAwait = teleportAwaitField!!.getInt(connection) + 1
            if (teleportAwait == 2147483647) teleportAwait = 0
            teleportAwaitField!!.set(connection, teleportAwait)
            awaitingTeleportTimeField!!.set(connection, 0)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        connection.sendPacket(ClientboundPlayerPositionPacket(x, y, z, 0f, 0f, TELEPORT_FLAGS, teleportAwait))
    }
}