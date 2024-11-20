package me.onlyjordon.pressed.fakerank

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import net.luckperms.api.query.QueryOptions
import org.bukkit.entity.Player

object FakeRankManager {

    enum class Rank(val group: String) {
        DEFAULT("default")
    }

    fun applyFakeRank(player: Player, rank: Rank) {
        val api = LuckPermsProvider.get()
        val user: User = api.getPlayerAdapter(Player::class.java).getUser(player)
        if (user.nodes.any { it.key.startsWith("temp.rank.") }) {
            resetFakeRank(player)
        }
        val group = user.primaryGroup

        user.setPrimaryGroup(rank.group)
        user.getInheritedGroups(QueryOptions.nonContextual()).forEach {

            val perms = api.groupManager.getGroup(group)!!.nodes
                .filter { NodeType.PERMISSION.matches(it) || NodeType.REGEX_PERMISSION.matches(it) }
            perms.forEach {
                user.data().add(it)
            }
        }
        user.nodes.stream().filter { NodeType.INHERITANCE.matches(it) }.forEach {
            user.data().add(Node.builder("temp.rank.${it.key}").build())
            user.data().remove(it)
        }
        api.userManager.saveUser(user)
    }

    fun resetFakeRank(player: Player) {
        val api = LuckPermsProvider.get()
        val user: User = api.getPlayerAdapter(Player::class.java).getUser(player)
        val ranks = user.nodes.filter { it.key.startsWith("temp.rank.") }
        user.nodes.filter { !NodeType.INHERITANCE.matches(it) }.forEach { user.data().remove(it) }
        ranks.forEach {
            user.data().add(Node.builder(it.key.replace("temp.rank.", "")).build())
        }
        api.userManager.saveUser(user)
    }
}