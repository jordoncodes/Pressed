package me.onlyjordon.pressed.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.max

class StackBuilder(val type: Material) {
    private var name: Component? = null
    private var hideEnchants: Boolean = false
    private var amount = 1
    private var giveKB = false
    private var lores: MutableList<Component> = ArrayList()
    private var unbreakable = false
    private var maxStackSize = -1

    fun named(name: Component): StackBuilder {
        this.name = name
        return this
    }

    fun unbreakable(unbreakable: Boolean): StackBuilder {
        this.unbreakable = unbreakable
        return this
    }

    fun lore(vararg components: Component): StackBuilder {
        lores = components.toMutableList()
        return this
    }

    fun lore(components: MutableList<Component>): StackBuilder {
        lores = components
        return this
    }

    fun hideEnchants(): StackBuilder {
        this.hideEnchants = true
        return this
    }

    fun amount(amount: Int): StackBuilder {
        this.amount = amount
        return this
    }

    fun giveKB(): StackBuilder {
        this.giveKB = true
        return this
    }

    fun build(): ItemStack {
        val i = ItemStack(type, amount)
        if (type == Material.AIR) return i
        val meta = i.itemMeta
        if (name != null) meta.displayName(name!!.style(name!!.style().toBuilder().colorIfAbsent(TextColor.color(ChatColor.WHITE.color.rgb)).decoration(TextDecoration.ITALIC, false)))
        if (hideEnchants) meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        if (unbreakable) meta.isUnbreakable = true
        if (lores.isNotEmpty()) meta.lore(lores.map { it.style(it.style().toBuilder().colorIfAbsent(TextColor.color(ChatColor.WHITE.color.rgb)).decoration(TextDecoration.ITALIC, false)) })
        if (maxStackSize != -1) meta.setMaxStackSize(maxStackSize)
        i.itemMeta = meta
        if (giveKB) i.addUnsafeEnchantments(mutableMapOf(Enchantment.KNOCKBACK to 2))

        return i
    }

    fun maxStackSize(i: Int): StackBuilder {
        maxStackSize = i
        return this
    }

}