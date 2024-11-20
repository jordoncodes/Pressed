package me.onlyjordon.pressed.cosmetics.shop

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import me.onlyjordon.pressed.cosmetics.BlockType
import me.onlyjordon.pressed.cosmetics.CosmeticType
import me.onlyjordon.pressed.cosmetics.HelmetType
import me.onlyjordon.pressed.cosmetics.StickType
import me.onlyjordon.pressed.stats.User
import me.onlyjordon.pressed.util.StackBuilder
import me.onlyjordon.pressed.util.UsefulFunctions.plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import net.minecraft.world.item.DebugStickItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.NumberFormat

class CosmeticShopGui(val user: User) {

    val gui = Gui.gui().rows(1).title(miniMessage().deserialize("<light_purple>Cosmetics</light_purple>")).create()
    init {
        for (i in 0 until 9) {
            gui.setItem(i, ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(miniMessage().deserialize("<gray></gray>")).asGuiItem { it.isCancelled = true })
        }

        setShopItem(plugin.shopItemHelmet, plugin.shopItemHelmet.stack, "(helmet)", 3)
        setShopItem(plugin.shopItemBlock, plugin.shopItemBlock.stack, "(block set)", 4)
        setShopItem(plugin.shopItemStick, plugin.shopItemStick.stack, "(stick)", 5)
    }

    fun open() {
        (user.player as? Player)?.let { gui.open(it) }
    }

    fun setShopItem(shopItem: CosmeticType, stack: ItemStack, displayNameAddition: String, slot: Int) {
        val itemMeta = stack.itemMeta
        val itemLore = itemMeta.lore() ?: ArrayList()
        val displayColor = if (user.ownedHelmets.contains(shopItem) || user.ownedBlocks.contains(shopItem) || user.ownedSticks.contains(shopItem)) "<aqua>" else if (user.coins >= shopItem.price) "<green>" else "<red>"
        itemMeta.displayName((itemMeta.displayName() ?: Component.text("")).append(miniMessage().deserialize(" <white>$displayNameAddition</white>")))
        itemLore.add(miniMessage().deserialize(String.format("%sPrice: <yellow>%s Coins</yellow>", displayColor, NumberFormat.getNumberInstance().format(shopItem.price))))
        itemMeta.lore(itemLore)
        stack.itemMeta = itemMeta
        gui.setItem(slot, ItemBuilder.from(stack).asGuiItem { event ->
            event.isCancelled = true
            val stick = shopItem is StickType
            val helmet = shopItem is HelmetType
            val block = shopItem is BlockType
            if (user.ownedSticks.contains(shopItem) || user.ownedBlocks.contains(shopItem) || user.ownedHelmets.contains(shopItem)) {
                event.whoClicked.sendMessage(miniMessage().deserialize("<red>You already own this!</red>"))
                return@asGuiItem
            }
            if (user.coins >= shopItem.price) {
                if (stick) user.ownedSticks.add(plugin.shopItemStick)
                if (helmet) user.ownedHelmets.add(plugin.shopItemHelmet)
                if (block) user.ownedBlocks.add(plugin.shopItemBlock)
                event.whoClicked.sendMessage(miniMessage().deserialize(String.format("<light_purple>You now own a <green>%s</green>!</light_purple>", shopItem.displayName)))
                user.coins -= shopItem.price.toLong()
            } else {
                event.whoClicked.sendMessage(miniMessage().deserialize("<red>You don't have enough <yellow>Coins</yellow> to buy this!</red>"))
            }
        })
    }

}