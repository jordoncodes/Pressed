package me.onlyjordon.pressed.quests.gui

import me.onlyjordon.pressed.stats.User
import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Bukkit
import org.bukkit.Material

class QuestGui(val user: User) {

    companion object {
        val killItem = StackBuilder(Material.DIAMOND_SWORD).named(miniMessage().deserialize("<green>Kill Quests")).build()
        val ksItem = StackBuilder(Material.NETHERITE_SWORD).named(miniMessage().deserialize("<green>Killstreak Quests")).build()
        val bpItem = StackBuilder(Material.LIME_WOOL).named(miniMessage().deserialize("<green>Block Place Quests")).build()
    }

    val holder = QuestInventoryHolder()
    val gui = Bukkit.createInventory(holder, 9*6, miniMessage().deserialize("<dark_purple>Quests"))
    init {
        holder.inv = gui

        for (i in 0 until 9*6) {
            gui.setItem(i, StackBuilder(Material.GRAY_STAINED_GLASS_PANE).named(miniMessage().deserialize("<gray></gray>")).build())
        }
        gui.setItem(20, killItem)
        gui.setItem(22, ksItem)
        gui.setItem(24, bpItem)

    }

    fun open() {
        user.player.player?.openInventory(gui)
    }

}