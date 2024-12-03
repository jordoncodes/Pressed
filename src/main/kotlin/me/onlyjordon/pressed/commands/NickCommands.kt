package me.onlyjordon.pressed.commands

import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.optionalArguments
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import me.onlyjordon.pressed.fakerank.FakeRankManager
import me.onlyjordon.pressed.nick.NickListener
import me.onlyjordon.pressed.util.NickNamer
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object NickCommands {
    private val noOtherPermsMessage: String = ChatColor.translateAlternateColorCodes('&', "&cYou may only do this to yourself!")

    fun register() {
//        commandAPICommand("fakerank") {
//            permission = CommandPermission.fromString("pressed.fakerank")
//            stringArgument("rank")
//            playerExecutor { player, args ->
//                val r = args["rank"] as String
//                try {
//                    val rank = FakeRankManager.Rank.valueOf(r.uppercase())
//                    FakeRankManager.applyFakeRank(player, rank)
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                    player.sendMessage("Invalid rank!")
//                }
//            }
//        }
//
//        commandAPICommand("removefakerank") {
//            permission = CommandPermission.fromString("pressed.fakerank")
//            playerExecutor { player, _ ->
//                val api = LuckPermsProvider.get()
//                val user: User = api.getPlayerAdapter(Player::class.java).getUser(player)
//                user.nodes.filter { it.key.startsWith("temp.rank.") }.forEach {
//                    user.data().add(Node.builder(it.key.replace("temp.rank.", "")).build())
//                    api.userManager.saveUser(user)
//                }
//                FakeRankManager.resetFakeRank(player)
//            }
//        }
//        commandAPICommand("nick") {
//            permission = CommandPermission.fromString("pressed.nick")
//            stringArgument("name")
//            optionalArguments(PlayerArgument("other"))
//            playerExecutor { player, args ->
//                val name = args["name"] as String
//                if (Bukkit.getPlayer(name) != null || NickListener.nameMap.values.any { it.equals(name, true) }) {
//                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat name is already in use!"))
//                    return@playerExecutor
//                }
//                val other = args["other"] as Player?
//                if (other != null) {
//                    if (player.hasPermission("pressed.nick.other")) {
//                        NickNamer.setNickname(other, name, true)
//
//                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dNick of &5${other.name}&d has been set to &5$name&d!"))
//                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Your&d nick has been set to &5$name&d!"))
//                        return@playerExecutor
//                    } else {
//                        player.sendMessage(noOtherPermsMessage)
//                        return@playerExecutor
//                    }
//                }
//                NickNamer.setNickname(player, name, true)
//                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Your&d nick has been set to &5$name&d!"))
//            }
//        }
//        commandAPICommand("unnick") {
//            permission = CommandPermission.fromString("pressed.nick")
//            optionalArguments(PlayerArgument("other"))
//            playerExecutor { player, args ->
//                val otherPlayer = args.get("other") as Player?
//                if (otherPlayer != null) {
//                    if (player.hasPermission("pressed.nick.other")) {
//                        NickNamer.reset(otherPlayer)
//                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dNick of &5${otherPlayer.name}&d has been &5reset&d!"))
//                        otherPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Your&d nick has been &5reset&d!"))
//                        return@playerExecutor
//                    } else {
//                        player.sendMessage(noOtherPermsMessage)
//                        return@playerExecutor
//                    }
//                }
//                NickNamer.reset(player)
//            }
//
//            commandAPICommand("skin") {
//                permission = CommandPermission.fromString("pressed.skin")
//                stringArgument("name")
//                optionalArguments(PlayerArgument("other"))
//                playerExecutor { player, args ->
//                    val name = args["name"] as String
//                    val otherPlayer = args.get("other") as Player?
//                    if (otherPlayer != null) {
//                        if (player.hasPermission("pressed.skin.other")) {
//                            NickNamer.setSkin(otherPlayer, name)
//                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dSkin of &5${otherPlayer.name}&d has been set to &5$name&d!"))
//                            otherPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Your&d skin has been set to &5$name&d!"))
//                        } else {
//                            player.sendMessage(noOtherPermsMessage)
//                        }
//                    } else {
//                        NickNamer.setSkin(player, name)
//                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Your&d skin has been set to &5$name&d!"))
//                    }
//                }
//            }
//        }
    }
}