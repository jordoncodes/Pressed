package me.onlyjordon.pressed.cosmetics

import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class HelmetType(override val price: Double, override val rarity: ItemRarity, val type: Material, override val displayName: String, override val description: MutableList<String>): CosmeticType {
    DEFAULT(0.0, ItemRarity.COMMON, Material.AIR, "", mutableListOf<String>()),
    GLASS(500.0, ItemRarity.COMMON, Material.GLASS, "<gray>GLASS</gray>", mutableListOf("<gray>You look like an astronaut!</gray>")),
    CLOUD(1000.0, ItemRarity.UNCOMMON, Material.WHITE_STAINED_GLASS, "<yellow>CLOUD</yellow>", mutableListOf("<gray>Wearing a cloud on your head?</gray>")),
    ANGER(2000.0, ItemRarity.LEGENDARY, Material.RED_STAINED_GLASS, "<gold>ANGER</gold>", mutableListOf("<gray>Do you need Anger Management?</gray>")),
    DOGGY(5000.0, ItemRarity.MYTHIC, Material.BONE, "<dark_purple>DOGGY</dark_purple>", mutableListOf("<gray>The mythical dog bone</gray>")),
    UNICORN(5000.0, ItemRarity.EPIC, Material.END_ROD, "<light_purple>UNICORN</light_purple>", mutableListOf("<gray>You're now a unicorn!</gray>")),
    SPYGLASS(5000.0, ItemRarity.UNCOMMON, Material.SPYGLASS, "<yellow>SPYGLASS</yellow>", mutableListOf("<gray>Zoom zoom</gray>")),
    FISH_SLAP(5000.0, ItemRarity.LEGENDARY, Material.COD, "<gold>FISH SLAP</gold>", mutableListOf("<gray>Stop that!</gray>")),
    ANTENNA(5000.0, ItemRarity.RARE, Material.LIGHTNING_ROD, "<aqua>ANTENNA</aqua>", mutableListOf("<gray>*crackles* where are-*cuts off*</gray>")),
    GLASSES(1000.0, ItemRarity.RARE, Material.OAK_FENCE_GATE, "<aqua>GLASSES</aqua>", mutableListOf("<gray>Now I can see better... I think?</gray>")),
    BED(500.0, ItemRarity.UNCOMMON, Material.RED_BED, "<yellow>BED</yellow>", mutableListOf("<gray>Bedhead</gray>")),
    MONOCLE(5000.0, ItemRarity.COMMON, Material.LEAD, "<gray>MONOCLE</gray>", mutableListOf("<gray>Only works on some skins</gray>")),
    TIARA(5000.0, ItemRarity.RARE, Material.MEDIUM_AMETHYST_BUD, "<aqua>TIARA</aqua>", mutableListOf("<gray>Pretty pretty</gray>")),
    OCEAN(1000.0, ItemRarity.COMMON, Material.BLUE_STAINED_GLASS, "<gray>OCEAN</gray>", mutableListOf("<gray>Because it's blue</gray>")),
    PVPER(500.0, ItemRarity.UNCOMMON, Material.DIAMOND_SWORD, "<yellow>PVPER</yellow>", mutableListOf("<gray>Kill those players</gray>")),
    THE_STICK(1000.0, ItemRarity.COMMON, Material.STICK, "<gray>THE STICK</gray>", mutableListOf("<gray>Why are you wearing a stick?</gray>")),
    ;


    override val stack: ItemStack
        get() = StackBuilder(type).unbreakable(true).named(Component.text("").style(Style.style()).append(miniMessage().deserialize(displayName))).lore(*description.map { Component.text("").style(Style.style()).append(miniMessage().deserialize(it)) }.toTypedArray(), Component.text("").style(Style.style()).append(miniMessage().deserialize(rarity.displayName))).build()

}