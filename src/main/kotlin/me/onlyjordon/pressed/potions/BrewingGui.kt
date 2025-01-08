package me.onlyjordon.pressed.potions

import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import me.onlyjordon.pressed.stats.User
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class BrewingGui(val user: User) {
    var potionEffects = mutableListOf(
        PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, true) to 5,
        PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, true) to 12,
        PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 0, true, true) to 5,
        PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 1, true, true) to 12,
        PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, true) to 100,

        PotionEffect(PotionEffectType.SPEED, 2400, 0, true, true) to 2,
        PotionEffect(PotionEffectType.SPEED, 2400, 1, true, true) to 5,
        PotionEffect(PotionEffectType.JUMP_BOOST, 2400, 0, true, true) to 2,
        PotionEffect(PotionEffectType.JUMP_BOOST, 2400, 1, true, true) to 5,
        PotionEffect(PotionEffectType.INVISIBILITY, 2400, 0, true, true) to 18,
    )

    var activePotions = mutableListOf<Pair<PotionEffect, Int>>()

    fun open() {
        (user.player as? Player)?.let { gui.open(it) }
    }

    val gui = Gui.gui().title(miniMessage().deserialize("<yellow>Potion</yellow> <light_purple>Shop</light_purple>")).rows(1).create()
    init {
        gui.setItem(4, ItemBuilder.from(Material.GLASS_BOTTLE).name(miniMessage().deserialize("<!i><yellow>Select</yellow> <dark_purple>Potion</dark_purple>")).asGuiItem { event ->
            clear()
            event.isCancelled = true
            activePotions.clear()
            activePotions.add(potionEffects.random())
            potionEffects.removeAll(activePotions)
            activePotions.add(potionEffects.random())
            potionEffects.removeAll(activePotions)
            activePotions.add(potionEffects.random())
            potionEffects.clear()
            activePotions.forEachIndexed { index, (effect, cost) ->
                val seconds: Long = (effect.duration / 20).toLong()
                val DD: Long = seconds / 86400
                val HH: Long = (seconds % 86400) / 3600
                val MM: Long = (seconds % 3600) / 60
                val SS: Long = seconds % 60
                val includeDays = DD > 0
                val includeHours = HH > 0 || includeDays
                var s = if (includeDays) String.format("%02d days %02d:%02d:%02d", DD, HH, MM, SS) else if (includeHours) String.format("%02d:%02d:%02d", HH, MM, SS) else String.format("%02d:%02d", MM, SS)
                if (effect.duration == Integer.MAX_VALUE) s = "Infinite"
                var item = ItemStack(Material.POTION)
                var meta = item.itemMeta as PotionMeta
                meta.basePotionType = PotionType.getByEffect(effect.type)
                item.itemMeta = meta
                gui.setItem(3 + index, ItemBuilder.from(item)
                    .name(miniMessage().deserialize("<yellow>${effect.type.name}</yellow> <green>Cost: $cost Blaze Powder</green>"))
                    .lore(miniMessage().deserialize("<yellow>Duration: ${s}</yellow>"), miniMessage().deserialize("<yellow>Amount: ${effect.amplifier+1}</yellow>"))
                    .asGuiItem { event ->
                        event.isCancelled = true
                        if (user.blazeDust >= cost) {
                            user.blazeDust -= cost.toLong()
                            event.whoClicked.addPotionEffect(effect)
                            gui.close(event.whoClicked)
                            event.whoClicked.sendMessage(miniMessage().deserialize("<yellow>You purchased a <green>${effect.type.name}</green> potion for <green>$cost Blaze Powder</green>!"))
                        } else {
                            event.whoClicked.sendMessage(miniMessage().deserialize("<red>You don't have enough <yellow>Blaze Powder</yellow> to buy this!</red>"))
                        }
                    })
            }
            gui.update()
        })
    }

    fun clear() {
        for (i in 0 until gui.rows*9) {
            if (i % 9 != 0) gui.removeItem(i)
        }
    }

}