package me.onlyjordon.pressed.cosmetics

import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class StickType(override val price: Double, override val rarity: ItemRarity, val type: Material, override val displayName: String, override val description: MutableList<String>): CosmeticType {

    DEFAULT(0.0, ItemRarity.COMMON, Material.STICK, "<gray><bold>DEFAULT</bold></gray>", mutableListOf("<gray>The default stick that</gray>", "<gray>everyone owns!</gray>")),
    SWORD(1000.0, ItemRarity.COMMON, Material.WOODEN_SWORD, "<gray>WOODEN SWORD</gray>", mutableListOf("<gray>The weakest of the swords.</gray>")),
    STONE_SWORD(1250.0, ItemRarity.UNCOMMON, Material.STONE_SWORD, "<yellow>STONE SWORD</yellow>", mutableListOf("<gray>Slightly better than the wooden sword.</gray>")),
    GOLDEN_SWORD(1500.0, ItemRarity.RARE, Material.GOLDEN_SWORD, "<aqua>GOLDEN SWORD</aqua>", mutableListOf("<gray>Gold is soft and malleable</gray>")),
    IRON_SWORD(2000.0, ItemRarity.EPIC, Material.IRON_SWORD, "<light_purple>IRON SWORD</light_purple>", mutableListOf("<gray>Kill your enemies quicker with intimidation</gray>")),
    DIAMOND_SWORD(10000.0, ItemRarity.LEGENDARY, Material.DIAMOND_SWORD, "<gold>DIAMOND SWORD</gold>", mutableListOf("<gray>IT'S A DIAMOND!</gray>")),
    NETHERITE_SWORD(12500.0, ItemRarity.MYTHIC, Material.NETHERITE_SWORD, "<dark_purple>NETHERITE SWORD</dark_purple>", mutableListOf("<gray>Netherite = hacks</gray>")),

    AXE(1000.0, ItemRarity.COMMON, Material.WOODEN_AXE, "<gray>WOODEN AXE</gray>", mutableListOf("<gray>The weakest of the axes.</gray>")),
    STONE_AXE(1250.0, ItemRarity.UNCOMMON, Material.STONE_AXE, "<yellow>STONE AXE</yellow>", mutableListOf("<gray>Slightly better than the wooden axe.</gray>")),
    GOLDEN_AXE(1500.0, ItemRarity.RARE, Material.GOLDEN_AXE, "<aqua>GOLDEN AXE</aqua>", mutableListOf("<gray>Gold is pretty useless</gray>")),
    IRON_AXE(2000.0, ItemRarity.EPIC, Material.IRON_AXE, "<light_purple>IRON AXE</light_purple>", mutableListOf("<gray>Intimidation!!</gray>")),
    DIAMOND_AXE(10000.0, ItemRarity.LEGENDARY, Material.DIAMOND_AXE, "<gold>DIAMOND AXE</gold>", mutableListOf("<gray>Diamonds!</gray>")),
    NETHERITE_AXE(12500.0, ItemRarity.MYTHIC, Material.NETHERITE_AXE, "<dark_purple>NETHERITE AXE</dark_purple>", mutableListOf("<gray>Netherite = Antikb</gray>")),

    LIT(1500.0, ItemRarity.UNCOMMON, Material.TORCH, "<yellow>LIT</yellow>", mutableListOf("<gray>Torch</gray>")),
    REDSTONE(1500.0, ItemRarity.UNCOMMON, Material.REDSTONE_TORCH, "<yellow>REDSTONE</yellow>", mutableListOf("<gray>Torch</gray>")),
    SWITCH(1500.0, ItemRarity.RARE, Material.LEVER, "<aqua>SWITCH</aqua>", mutableListOf("<gray>Is it really though?</gray>")),
    SHARD(1500.0, ItemRarity.EPIC, Material.ECHO_SHARD, "<light_purple>SHARD</light_purple>", mutableListOf("<gray>Shiny!</gray>")),
    AMETHYST(1500.0, ItemRarity.EPIC, Material.AMETHYST_SHARD, "<light_purple>AMETHYST</light_purple>", mutableListOf("<gray>Shard</gray>")),
    NETHER_STAR(1500.0, ItemRarity.LEGENDARY, Material.NETHER_STAR, "<gold>NETHER STAR</gold>", mutableListOf("<gray>Mum, I beat the wither!</gray>")),
    STAR(1500.0, ItemRarity.EPIC, Material.FIREWORK_STAR, "<light_purple>STAR</light_purple>", mutableListOf("<gray>Fireworks</gray>")),
    CRYING(1500.0, ItemRarity.RARE, Material.GHAST_TEAR, "<aqua>CRYING</aqua>", mutableListOf("<gray>The tears of the Ghast</gray>")),
    ITS_SUGAR(1500.0, ItemRarity.MYTHIC, Material.SUGAR, "<dark_purple>IT'S SUGAR</dark_purple>", mutableListOf("<gray>I swear!!!</gray>")),
    GLOWS(1500.0, ItemRarity.MYTHIC, Material.GLOWSTONE, "<dark_purple>IT GLOWS</dark_purple>", mutableListOf("<gray>Oh really?</gray>")),
    OMINOUS_KEY(1500.0, ItemRarity.LEGENDARY, Material.OMINOUS_TRIAL_KEY, "&d&lOMINOUS KEY", mutableListOf("<gray>What does it do?</gray>")),
    OTHERSIDE(2500.0, ItemRarity.LEGENDARY, Material.MUSIC_DISC_OTHERSIDE, "<gold>OTHERSIDE?</gold>", mutableListOf("<gray>Put it in the jukebox!</gray>")),
    SCARY_DISC(2500.0, ItemRarity.LEGENDARY, Material.MUSIC_DISC_11, "<gold>SCARY DISC</gold>", mutableListOf("<gray>Is it really though?</gray>")),
    FRAGMENT(2500.0, ItemRarity.UNCOMMON, Material.DISC_FRAGMENT_5, "<yellow>FRAGMENT</yellow>", mutableListOf("<gray>It seems to have shattered.</gray>")),
    FISHSTICK(2500.0, ItemRarity.EPIC, Material.COD, "<light_purple>FISHSTICK</light_purple>", mutableListOf("<gray>Fish slap!</gray>"))
    ;

    override val stack: ItemStack
        get() = StackBuilder(type).unbreakable(true).giveKB().named(Component.text("").style(Style.style()).append(miniMessage().deserialize(displayName))).lore(*description.map { Component.text("").style(Style.style()).append(miniMessage().deserialize(it)) }.toTypedArray(), Component.text("").style(Style.style()).append(miniMessage().deserialize(rarity.displayName))).build()

}