package me.onlyjordon.pressed.quests.daily.gui

import me.onlyjordon.pressed.quests.BlocksPlacedQuest
import me.onlyjordon.pressed.quests.KillsQuest
import me.onlyjordon.pressed.quests.KillstreakQuest
import me.onlyjordon.pressed.quests.Quest
import me.onlyjordon.pressed.quests.daily.gui.DailyQuestGui.Companion.bpItem
import me.onlyjordon.pressed.quests.daily.gui.DailyQuestGui.Companion.killItem
import me.onlyjordon.pressed.quests.daily.gui.DailyQuestGui.Companion.ksItem
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.StackBuilder
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import java.text.NumberFormat

class DailyQuestGuiListener: Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory.holder !is DailyQuestInventoryHolder) return

        val player = e.whoClicked as Player
        e.isCancelled = true
        if (e.currentItem == killItem) {
            populateQuests(player, e.inventory, plugin.dailyKillQuest)
        }

        if (e.currentItem == ksItem) {
            populateQuests(player, e.inventory, plugin.dailyKillstreakQuest)
        }

        if (e.currentItem == bpItem) {
            populateQuests(player, e.inventory, plugin.dailyBlocksPlacedQuest)
        }
    }

    fun populateQuests(player: Player, gui: Inventory, quest: Quest) {
        //TODO fix for daily (it shows total values so is currently unused)
        val user = UserManager.getUser(player.uniqueId)
        quest.levels.forEachIndexed { i, it ->
            quest.checkRequirement(player)
            val complete = quest.completeLevels.get(player.uniqueId).contains(it)
            gui.setItem(i, StackBuilder(if (complete) Material.GREEN_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE)
                .named(miniMessage().deserialize("<light_purple>${NumberFormat.getNumberInstance().format(it)} ${quest.name}</light_purple>"))
                .lore(mutableListOf(
                    miniMessage().deserialize(if (complete) "<green>Complete"
                    else if (quest is KillsQuest) "<red>${NumberFormat.getNumberInstance().format(user.kills)}/${NumberFormat.getNumberInstance().format(it)}"
                    else if (quest is KillstreakQuest) "<red>${NumberFormat.getNumberInstance().format(user.killstreak)}/${NumberFormat.getNumberInstance().format(it)}"
                    else if (quest is BlocksPlacedQuest) "<red>${NumberFormat.getNumberInstance().format(user.blocksPlaced)}/${NumberFormat.getNumberInstance().format(it)}"
                    else "<red>Unknown/$it"),
                ))
                .build())
        }
    }

}