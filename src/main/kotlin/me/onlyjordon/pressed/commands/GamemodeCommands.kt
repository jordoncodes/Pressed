package me.onlyjordon.pressed.commands

import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.optionalArguments
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import net.md_5.bungee.api.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player

object GamemodeCommands {
    private val gamemodeSetMessage = ChatColor.translateAlternateColorCodes('&', "&5Gamemode &dof &5%s&d has been set to &5%s&d.")
    private val noPermsMessage = ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to do this!")
    private val noOtherPermsMessage = ChatColor.translateAlternateColorCodes('&', "&cYou may only do this to yourself!")

    fun register() {
        commandAPICommand("gm") {
            permission = CommandPermission.fromString("pressed.gamemode")
            stringArgument("mode") {
                replaceSuggestions(ArgumentSuggestions.strings("0","1","2","3","survival","creative","adventure","spectator","s","c","a","sp"))
            }
            optionalArguments(PlayerArgument("otherPlayer"))
            aliases = arrayOf("gamemode")
            playerExecutor { player, args ->
                val gm = args.get("mode") as String
                val otherPlayer = args.get("otherPlayer") as Player?
                if (gm == "creative" || gm == "c" || gm == "1") {
                    if (player.hasPermission("pressed.gamemode.creative")) {
                        if (otherPlayer != null) {
                            if (player.hasPermission("pressed.gamemode.other")) {
                                player.gameMode = GameMode.CREATIVE
                                player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Creative"))
                                otherPlayer.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Creative"))
                            } else {
                                player.sendMessage(noOtherPermsMessage)
                            }
                        } else {
                            player.gameMode = GameMode.CREATIVE
                            player.sendMessage(gamemodeSetMessage.format(player.name, "Creative"))
                        }
                    } else {
                        player.sendMessage(noPermsMessage)
                    }
                }
                if (gm == "survival" || gm == "s" || gm == "0") {
                    if (player.hasPermission("pressed.gamemode.survival")) {
                        if (otherPlayer != null) {
                            if (player.hasPermission("pressed.gamemode.other")) {
                                player.gameMode = GameMode.SURVIVAL
                                player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Survival"))
                                otherPlayer.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Survival"))
                            } else {
                                player.sendMessage(noOtherPermsMessage)
                            }
                        } else {
                            player.gameMode = GameMode.SURVIVAL
                            player.sendMessage(gamemodeSetMessage.format(player.name, "Survival"))
                        }
                    } else {
                        player.sendMessage(noPermsMessage)
                    }
                }

                if (gm == "spectator" || gm == "sp" || gm == "3") {
                    if (player.hasPermission("pressed.gamemode.spectator")) {
                        if (otherPlayer != null) {
                            if (player.hasPermission("pressed.gamemode.other")) {
                                player.gameMode = GameMode.SPECTATOR
                                player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Spectator"))
                                otherPlayer.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Spectator"))
                            } else {
                                player.sendMessage(noOtherPermsMessage)
                            }
                        } else {
                            player.gameMode = GameMode.SPECTATOR
                            player.sendMessage(gamemodeSetMessage.format(player.name, "Spectator"))
                        }
                    } else {
                        player.sendMessage(noPermsMessage)
                    }
                }

                if (gm == "adventure" || gm == "a" || gm == "2") {
                    if (player.hasPermission("pressed.gamemode.adventure")) {
                        if (otherPlayer != null) {
                            if (player.hasPermission("pressed.gamemode.other")) {
                                player.gameMode = GameMode.ADVENTURE
                                player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Adventure"))
                                otherPlayer.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Adventure"))
                            } else {
                                player.sendMessage(noOtherPermsMessage)
                            }
                        } else {
                            player.gameMode = GameMode.ADVENTURE
                            player.sendMessage(gamemodeSetMessage.format(player.name, "Adventure"))
                        }
                    } else {
                        player.sendMessage(noPermsMessage)
                    }
                }
            }
        }

        commandAPICommand("gmc") {
            aliases = arrayOf("gm1", "gmcreative", "creativemode", "gamemodecreative", "gamemode1", "gamemodec")
            permission = CommandPermission.fromString("pressed.gamemode.creative")
            optionalArguments(PlayerArgument("otherPlayer"))
            playerExecutor { player, args ->
                val otherPlayer = args.get("otherPlayer") as Player?
                if (otherPlayer != null) {
                    if (player.hasPermission("pressed.gamemode.other")) {
                        otherPlayer.gameMode = GameMode.CREATIVE
                        player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Creative"))
                    } else {
                        player.sendMessage(noOtherPermsMessage)
                    }
                } else {
                    player.gameMode = GameMode.CREATIVE
                    player.sendMessage(gamemodeSetMessage.format(player.name, "Creative"))
                }
            }
        }

        commandAPICommand("gms") {
            aliases = arrayOf("gm0", "gmsurvival", "survivalmode", "gamemodesurvival", "gamemode0", "gamemodes")
            permission = CommandPermission.fromString("pressed.gamemode.survival")
            optionalArguments(PlayerArgument("otherPlayer"))
            playerExecutor { player, args ->
                val otherPlayer = args.get("otherPlayer") as Player?
                if (otherPlayer != null) {
                    if (player.hasPermission("pressed.gamemode.other")) {
                        otherPlayer.gameMode = GameMode.SURVIVAL
                        player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Survival"))
                    } else {
                        player.sendMessage(noOtherPermsMessage)
                    }
                } else {
                    player.gameMode = GameMode.SURVIVAL
                    player.sendMessage(gamemodeSetMessage.format(player.name, "Survival"))
                }
            }
        }

        commandAPICommand("gmsp") {
            aliases = arrayOf("gm3", "gmspectator", "spectatormode", "gamemodespectator", "gamemode3", "gamemodesp")
            permission = CommandPermission.fromString("pressed.gamemode.spectator")
            optionalArguments(PlayerArgument("otherPlayer"))
            playerExecutor { player, args ->
                val otherPlayer = args.get("otherPlayer") as Player?
                if (otherPlayer != null) {
                    if (player.hasPermission("pressed.gamemode.other")) {
                        otherPlayer.gameMode = GameMode.SPECTATOR
                        player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Spectator"))
                    } else {
                        player.sendMessage(noOtherPermsMessage)
                    }
                } else {
                    player.gameMode = GameMode.SPECTATOR
                    player.sendMessage(gamemodeSetMessage.format(player.name, "Spectator"))
                }
            }
        }

        commandAPICommand("gma") {
            aliases = arrayOf("gm2", "gmadventure", "adventuremode", "gamemodeadventure", "gamemode2", "gamemodea")
            permission = CommandPermission.fromString("pressed.gamemode.adventure")
            optionalArguments(PlayerArgument("otherPlayer"))
            playerExecutor { player, args ->
                val otherPlayer = args.get("otherPlayer") as Player?
                if (otherPlayer != null) {
                    if (player.hasPermission("pressed.gamemode.other")) {
                        otherPlayer.gameMode = GameMode.ADVENTURE
                        player.sendMessage(gamemodeSetMessage.format(otherPlayer.name, "Adventure"))
                    } else {
                        player.sendMessage(noOtherPermsMessage)
                    }
                } else {
                    player.gameMode = GameMode.ADVENTURE
                    player.sendMessage(gamemodeSetMessage.format(player.name, "Adventure"))
                }
            }
        }
    }
}