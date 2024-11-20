package me.onlyjordon.pressed.commands

import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.*
import me.onlyjordon.pressed.util.TeleportUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object TeleportCommands {

    fun register() {
        commandAPICommand("teleport") {
            permission = CommandPermission.fromString("pressed.teleport")
            aliases = arrayOf("tp")
            arguments(EntitySelectorArgument.OneEntity("other"))
            playerExecutor { player, args ->
                val entity = args.get("other") as Entity
                val loc = entity.location
                loc.pitch = entity.pitch
                loc.yaw = entity.yaw
                player.teleport(loc)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&5You&d have teleported to &5%s&d.", entity.name)))
            }
        }

        commandAPICommand("teleport") {
            permission = CommandPermission.fromString("pressed.teleport")
            arguments(EntitySelectorArgument.ManyEntities("entities"), EntitySelectorArgument.OneEntity("entity"))
            aliases = arrayOf("tp")
            anyExecutor { executor, args ->
                val entities = args.get("entities") as List<Entity>
                val entity = args.get("entity") as Entity
                entities.forEach {
                    if (it is Player) {
                        val loc = entity.location
                        loc.pitch = entity.pitch
                        loc.yaw = entity.yaw
                        it.teleport(loc)
                        return@forEach
                    }
                    it.teleport(entity)
                }
                if (entities.size > 1)
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&dTeleported &5%s entities&d to &5%s&d.", entities.count().toString(), entity.name)))
                else
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&dTeleported &5%s&d to &5%s&d.", entities.first().name, entity.name)))
            }
        }

        commandAPICommand("teleport") {
            permission = CommandPermission.fromString("pressed.teleport")
            arguments(LocationArgument("position"))
            aliases = arrayOf("tp")
            playerExecutor { player, args ->
                val loc = args.get("position") as Location
                TeleportUtils.teleport(player, loc)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&5You&d have teleported to &5%s&d.", "${loc.x.toInt()}, ${loc.y.toInt()}, ${loc.z.toInt()}")))
            }
        }

        commandAPICommand("teleport") {
            permission = CommandPermission.fromString("pressed.teleport")
            arguments(EntitySelectorArgument.ManyEntities("entities"), LocationArgument("position"))
            aliases = arrayOf("tp")
            anyExecutor { executor, args ->
                val entities = args.get("entities") as List<Entity>
                val position = args.get("position") as Location
                entities.forEach {
                    if (it is Player) {
                        TeleportUtils.teleport(it, position)
                        return@forEach
                    }
                    it.teleport(position)
                }
                val locString = "${position.x.toInt()}, ${position.y.toInt()}, ${position.z.toInt()}"
                if (entities.size > 1)
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&dTeleported &5%s entities&d to &5%s&d.", entities.count().toString(), locString)))
                else
                    executor.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&dTeleported &5%s&d to &5%s&d.", entities.first().name, locString)))
            }

        }


        commandAPICommand("tphere") {
            permission = CommandPermission.fromString("pressed.teleport")
            aliases = arrayOf("teleporthere", "tp2me")
            arguments(EntitySelectorArgument.OneEntity("other"))
            playerExecutor { player, args ->
                val entity = args.get("other") as Entity
                entity.teleport(player)
            }
        }
    }
}