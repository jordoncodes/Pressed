package me.onlyjordon.pressed.cosmetics.gui

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import me.onlyjordon.pressed.cosmetics.BlockType
import me.onlyjordon.pressed.cosmetics.CosmeticType
import me.onlyjordon.pressed.cosmetics.HelmetType
import me.onlyjordon.pressed.cosmetics.StickType
import me.onlyjordon.pressed.stats.User
import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CosmeticGui(val user: User) {
    fun open() {
        (user.player as? Player)?.let { gui.open(it) }
    }

    val gui = Gui.gui().title(miniMessage().deserialize("<yellow>Select</yellow> <light_purple>Cosmetics</light_purple>")).rows(6).create()
//    val gui = Bukkit.createInventory(holder, 9*6, )
    init {
//        holder.inv = gui
        gui.setItem(0, ItemBuilder.from(Material.STICK).name(miniMessage().deserialize("<!i><yellow>Select</yellow> <dark_purple>Stick</dark_purple>")).asGuiItem { event ->
            event.isCancelled = true
            clear()
            populate(user.ownedSticks)
        })
        gui.setItem(9, ItemBuilder.from(Material.LIME_WOOL).name(miniMessage().deserialize("<!i><yellow>Select</yellow> <dark_purple>Block Set</dark_purple>")).asGuiItem { event ->
            event.isCancelled = true
            clear()
            populate(user.ownedBlocks)

        })
        gui.setItem(18, ItemBuilder.from(Material.GLASS).name(miniMessage().deserialize("<!i><yellow>Select</yellow> <dark_purple>Helmet</dark_purple>")).asGuiItem { event ->
            event.isCancelled = true
            clear()
            populate(user.ownedHelmets)
        })
        val glass = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(miniMessage().deserialize("<!i><gray></gray>")).asGuiItem { e ->
            e.isCancelled = true
        }
        gui.setItem(27, glass)
        gui.setItem(36, glass)
        gui.setItem(45, glass)
        populate(user.ownedSticks)
    }

    fun clear() {
        for (i in 0 until gui.rows*9) {
            if (i % 9 != 0) gui.removeItem(i)
        }
    }

    fun populate(ownedItems: List<CosmeticType>) {
        var helmet = false
        for ((i, cosmetic) in ownedItems.withIndex()) {
            val isCurr = (user.helmet == cosmetic || user.stick == cosmetic || user.blockType == cosmetic)
            if (cosmetic is HelmetType) helmet = true
            if (cosmetic.stack.type == Material.AIR) continue
            gui.setItem(1 + i/8 + i, ItemBuilder.from(cosmetic.stack).glow(isCurr).asGuiItem { e ->
                e.isCancelled = true
                if (cosmetic is HelmetType)
                    user.helmet = cosmetic
                if (cosmetic is StickType)
                    user.stick = cosmetic
                if (cosmetic is BlockType)
                    user.blockType = cosmetic
                e.whoClicked.sendMessage(miniMessage().deserialize("<green>You have selected ${miniMessage().serialize(cosmetic.stack.displayName())}!</green>"))
                populate(ownedItems)
            })
        }
        if (helmet) {
            gui.removeItem(1)
            val isCurr = user.helmet == HelmetType.DEFAULT
            gui.setItem(1, ItemBuilder.from(Material.BARRIER).glow(isCurr).name(miniMessage().deserialize("<!i><red>None</red>")).asGuiItem { e ->
                e.isCancelled = true
                user.helmet = HelmetType.DEFAULT
                e.whoClicked.sendMessage(miniMessage().deserialize("<green>You have selected ${miniMessage().serialize(user.helmet.stack.displayName())}!</green>"))
            })
        }
        gui.update()
    }
}