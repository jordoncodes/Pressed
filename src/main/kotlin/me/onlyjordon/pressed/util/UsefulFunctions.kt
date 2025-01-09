package me.onlyjordon.pressed.util

import com.sk89q.worldedit.EditSessionBuilder
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import me.onlyjordon.pressed.Pressed
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.util.*


object UsefulFunctions {
    val plugin = JavaPlugin.getPlugin(Pressed::class.java)

    fun isInArena(loc: Location): Boolean {
        return loc.y < plugin.kitheight
    }

    fun Triple<Int, Int, Int>.toLocation(world: UUID): Location {
        return Location(Bukkit.getWorld(world), first.toDouble(), second.toDouble(), third.toDouble())
    }

    fun Triple<Int, Int, Int>.toLocation(world: World): Location {
        return Location(world, first.toDouble(), second.toDouble(), third.toDouble())
    }

    val UUID.asPlayer: Player?
        get() = Bukkit.getPlayer(this)


    fun Player.isInArena(): Boolean {
        return location.y < JavaPlugin.getPlugin(Pressed::class.java).kitheight
    }

    fun color(string: String): String {
        return ChatColor.translateAlternateColorCodes('&', string)
    }

    val Player.isInArenaWorld: Boolean
        get() = world == plugin.world
    val Location.isInArenaWorld: Boolean
        get() = world == plugin.world

    fun copyWorld(source: File, target: File) {
        try {
            val ignore = ArrayList(Arrays.asList("uid.dat", "session.dat"))
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists()) target.mkdirs()
                    val files: Array<String> = source.list()
                    for (file in files) {
                        val srcFile = File(source, file)
                        val destFile = File(target, file)
                        copyWorld(srcFile, destFile)
                    }
                } else {
                    val input: InputStream = FileInputStream(source)
                    val out: OutputStream = FileOutputStream(target)
                    val buffer = ByteArray(1024)
                    var length: Int
                    while ((input.read(buffer).also { length = it }) > 0) out.write(buffer, 0, length)
                    input.close()
                    out.close()
                }
            }
        } catch (e: IOException) {
        }
    }

    fun pasteSchematic(location: Location, schematicName: String) {
        try {
            val file = File(plugin.dataFolder, "$schematicName.schem")
            if (!file.exists()) {
                Bukkit.getLogger().warning("Schematic file $schematicName.schem not found in the plugin folder!")
                return
            }

            val format = ClipboardFormats.findByFile(file)
            if (format == null) {
                Bukkit.getLogger().warning("Unsupported schematic format!")
                return
            }

            format.getReader(file.inputStream()).use { reader: ClipboardReader ->
                val clipboard = reader.read()

                val weWorld: com.sk89q.worldedit.world.World = BukkitAdapter.adapt(location.world)

                val editSession = WorldEdit.getInstance().newEditSession(weWorld)

                val op = ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(location.blockX, location.blockY, location.blockZ))
                    .ignoreAirBlocks(true)
                    .build()
                Operations.complete(op)
            }

            Bukkit.getLogger().info("Schematic $schematicName.schem has been successfully pasted at (${location.blockX}, ${location.blockY}, ${location.blockZ}).")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}