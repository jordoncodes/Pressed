package me.onlyjordon.pressed.util

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
}