package me.onlyjordon.pressed.kiteditor

import me.onlyjordon.pressed.stats.User
import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class KitEditorGui(val user: User) {

    val holder = KitEditorInventoryHolder()
    val gui = Bukkit.createInventory(holder, 9*2, ChatColor.translateAlternateColorCodes('&', "&5Kit Editor"))
    val blockItem = user.blockType.stack
    val pearlItem = StackBuilder(Material.ENDER_PEARL).maxStackSize(99).named(miniMessage().deserialize("<aqua>ENDER PEARL</aqua>")).build()
    val webItem = StackBuilder(Material.COBWEB).maxStackSize(99).named(miniMessage().deserialize("<white>COBWEB</white>")).build()
    init {
        holder.inv = gui

        for (i in 9 until 18) {
            gui.setItem(i, StackBuilder(Material.GRAY_STAINED_GLASS_PANE).hideEnchants().named(miniMessage().deserialize("<gray></gray>")).build())
        }
        gui.setItem(16, ItemStack(Material.AIR))
        gui.setItem(if (user.stickSlot == 40) 16 else user.stickSlot, user.stick.stack)
        gui.setItem(if (user.blockSlot == 40) 16 else user.blockSlot, blockItem)
        gui.setItem(if (user.pearlSlot == 40) 16 else user.pearlSlot, pearlItem)
        gui.setItem(if (user.webSlot == 40) 16 else user.webSlot, webItem)

        gui.setItem(9, StackBuilder(Material.CHEST).hideEnchants().named(miniMessage().deserialize("<green>SAVE</green>")).build())
        gui.setItem(10, StackBuilder(Material.CLOCK).hideEnchants().named(miniMessage().deserialize("<gold>RESET TO DEFAULTS</gold>")).build())
        gui.setItem(17, StackBuilder(Material.BARRIER).hideEnchants().named(miniMessage().deserialize("<red>CLOSE</red>")).build())
    }

    fun open() {
        user.player.player?.openInventory(gui)
    }

}