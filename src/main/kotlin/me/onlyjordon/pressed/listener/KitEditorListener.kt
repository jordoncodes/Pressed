package me.onlyjordon.pressed.listener

import me.onlyjordon.pressed.kiteditor.KitEditorGui
import me.onlyjordon.pressed.kiteditor.KitEditorInventoryHolder
import me.onlyjordon.pressed.stats.UserManager
import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class KitEditorListener: Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory
        if (event.inventory.holder !is KitEditorInventoryHolder) return
        val player = event.whoClicked as Player
        if (event.slot == 17) player.closeInventory()
        if (event.slot == 9) {
            val user = UserManager.getUser(player.uniqueId)
            val blockItem = user.blockType.stack
            val pearlItem =
                StackBuilder(Material.ENDER_PEARL).maxStackSize(99).named(miniMessage().deserialize("<aqua>ENDER PEARL</aqua>")).build()
            val webItem =
                StackBuilder(Material.COBWEB).maxStackSize(99).named(miniMessage().deserialize("<white>COBWEB</white>")).build()
            clickedInventory?.forEachIndexed { i, item ->
                val slot = if (i == 16) 40 else i
                if (item != null && (i < 9 || i == 16)) {
                    if (item.isSimilar(blockItem)) {
                        user.blockSlot = slot
                    }
                    if (item.isSimilar(webItem)) {
                        user.webSlot = slot
                    }
                    if (item.isSimilar(pearlItem)) {
                        user.pearlSlot = slot
                    }
                    if (item.isSimilar(user.stick.stack)) {
                        user.stickSlot = slot
                    }
                }
            }
        }
        if (event.slot == 10) {
            val user = UserManager.getUser(player.uniqueId)
            user.stickSlot = 0
            user.blockSlot = 1
            user.webSlot = 2
            user.pearlSlot = 3
            player.closeInventory()
            KitEditorGui(user).open()
        }
        if (event.click.isShiftClick || event.click.isCreativeAction) {
            event.isCancelled = true
            return
        }
        if ((event.slot >= 9 || event.rawSlot >= 9) && event.rawSlot != 16) {
            event.isCancelled = true
            return
        }
        if (event.click.isKeyboardClick) {
            val item = clickedInventory?.getItem(event.hotbarButton)
            val otherItem = event.currentItem
            clickedInventory?.setItem(event.hotbarButton, otherItem)
            clickedInventory?.setItem(event.rawSlot, item)
            event.isCancelled = true
        }
        if (clickedInventory == event.view.bottomInventory) {
            event.isCancelled = true
        }
        if (clickedInventory?.type != InventoryType.CHEST) event.isCancelled = true
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is KitEditorInventoryHolder) return
        event.player.setItemOnCursor(ItemStack(Material.AIR))
        event.view.setCursor(ItemStack(Material.AIR))
    }
}