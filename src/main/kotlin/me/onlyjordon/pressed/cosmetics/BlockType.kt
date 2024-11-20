package me.onlyjordon.pressed.cosmetics

import me.onlyjordon.pressed.util.StackBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.inventory.ItemStack

enum class BlockType(override val price: Double, override val displayName: String, override val description: MutableList<String>, override val rarity: ItemRarity, vararg val blocks: BlockData): CosmeticType {
    DEFAULT(0.0, "<gray>DEFAULT</gray>", mutableListOf("<gray>The default block set that everyone</gray>", "<gray>has, nothing special.</gray>"), ItemRarity.COMMON, Material.LIME_WOOL.createBlockData(), Material.ORANGE_WOOL.createBlockData(), Material.RED_WOOL.createBlockData()),
    OCEAN(1500.0, "<gray>OCEAN</gray>", mutableListOf("<gray>A set of ocean-colored blocks</gray>"), ItemRarity.COMMON, Material.LIGHT_BLUE_WOOL.createBlockData(), Material.BLUE_WOOL.createBlockData(), Material.BLUE_STAINED_GLASS.createBlockData()),

    DARK_GLASS(1000.0, "<gray>DARK GLASS</gray>", mutableListOf("<gray>Dark variant of the glass pack</gray>"), ItemRarity.COMMON, Material.BLACK_STAINED_GLASS.createBlockData(), Material.GRAY_STAINED_GLASS.createBlockData(), Material.LIGHT_GRAY_STAINED_GLASS.createBlockData()),
    LIGHT_GLASS(1000.0, "<gray>LIGHT GLASS</gray>", mutableListOf("<gray>Light variant of the glass pack</gray>") ,ItemRarity.COMMON, Material.GLASS.createBlockData(), Material.WHITE_STAINED_GLASS.createBlockData(), Material.LIGHT_GRAY_STAINED_GLASS.createBlockData()),

    STONE(1500.0, "<gray>STONE</gray>", mutableListOf("<gray>Stone, polished andesite, then light gray concrete.</gray>"), ItemRarity.COMMON, Material.STONE.createBlockData(), Material.POLISHED_ANDESITE.createBlockData(), Material.LIGHT_GRAY_CONCRETE.createBlockData()),
    IRON(1500.0, "<gray>IRON</gray>", mutableListOf("<gray>The Iron block pack</gray>"), ItemRarity.COMMON, Material.IRON_ORE.createBlockData(), Material.RAW_IRON_BLOCK.createBlockData(), Material.IRON_BLOCK.createBlockData()),
    GOLD(1500.0, "<gray>GOLD</gray>", mutableListOf("<gray>The Gold block pack</gray>"), ItemRarity.COMMON, Material.GOLD_ORE.createBlockData(), Material.RAW_GOLD_BLOCK.createBlockData(), Material.GOLD_BLOCK.createBlockData()),
    LAPIS(1500.0, "<gray>LAPIS</gray>", mutableListOf("<gray>The Lapis block pack</gray>"), ItemRarity.COMMON, Material.LAPIS_ORE.createBlockData(), Material.LAPIS_BLOCK.createBlockData(), Material.BLUE_WOOL.createBlockData()),
    REDSTONE(1500.0, "<gray>REDSTONE</gray>", mutableListOf("<gray>The Redstone block pack</gray>"), ItemRarity.COMMON, Material.REDSTONE_ORE.createBlockData(), Material.REDSTONE_BLOCK.createBlockData(), Material.REDSTONE_LAMP.createBlockData()),
    DIAMOND(1500.0, "<gray>DIAMOND</gray>", mutableListOf("<gray>The Diamond block pack</gray>"), ItemRarity.COMMON, Material.DIAMOND_ORE.createBlockData(), Material.DIAMOND_BLOCK.createBlockData(), Material.LIGHT_BLUE_WOOL.createBlockData()),
    NETHERITE(1500.0, "<gray>NETHERITE</gray>", mutableListOf("<gray>The Netherite block pack</gray>"), ItemRarity.COMMON, Material.ANCIENT_DEBRIS.createBlockData(), Material.NETHERITE_BLOCK.createBlockData(), Material.GILDED_BLACKSTONE.createBlockData()),
    COPPER(1500.0, "<gray>COPPER</gray>", mutableListOf("<gray>The Copper block pack</gray>"), ItemRarity.COMMON, Material.COPPER_ORE.createBlockData(), Material.RAW_COPPER_BLOCK.createBlockData(), Material.COPPER_BLOCK.createBlockData()),
    WEATHERING_COPPER(2500.0, "<gray>WEATHERING COPPER</gray>", mutableListOf("<gray>Copper block pack but it's weathering</gray>"), ItemRarity.COMMON, Material.COPPER_BLOCK.createBlockData(), Material.EXPOSED_COPPER.createBlockData(), Material.WEATHERED_COPPER.createBlockData(), Material.OXIDIZED_COPPER.createBlockData()),

    SPONGE(1000.0, "<yellow>SPONGE</yellow>", mutableListOf("<gray>Spongy!</gray>"), ItemRarity.UNCOMMON, Material.SPONGE.createBlockData(), Material.WET_SPONGE.createBlockData(), Material.YELLOW_WOOL.createBlockData()),

    OVERWORLD(2500.0, "<aqua>OVERWORLD</aqua>", mutableListOf("<gray>Some blocks from the overworld!</gray>"), ItemRarity.RARE, Material.GRASS_BLOCK.createBlockData(), Material.OAK_WOOD.createBlockData(), Material.OAK_LEAVES.createBlockData()),
    NETHER(2500.0, "<aqua>NETHER</aqua>", mutableListOf("<gray>Some blocks from the nether!</gray>"), ItemRarity.RARE, Material.NETHERRACK.createBlockData(), Material.SOUL_SOIL.createBlockData(), Material.NETHER_BRICKS.createBlockData()),
    END(2500.0, "<aqua>END</aqua>", mutableListOf("<gray>Some blocks from the end!</gray>"), ItemRarity.RARE, Material.END_STONE.createBlockData(), Material.END_STONE_BRICKS.createBlockData(), Material.SMOOTH_SANDSTONE.createBlockData()),


    OAK(1500.0, "<gray>OAK</gray>", mutableListOf("<gray>Oak Wood was the First Wood Added to Minecraft</gray>"), ItemRarity.COMMON, Material.OAK_WOOD.createBlockData(), Material.STRIPPED_OAK_WOOD.createBlockData(), Material.OAK_PLANKS.createBlockData()),
    SPRUCE(1500.0, "<gray>SPRUCE</gray>", mutableListOf("<gray>Spruce was added in b1.2,</gray>", "<gray>stopped generating in release 2.1</gray>", "<gray>and since 1.2.2 generate again</gray>"), ItemRarity.COMMON, Material.SPRUCE_WOOD.createBlockData(), Material.STRIPPED_SPRUCE_WOOD.createBlockData(), Material.SPRUCE_PLANKS.createBlockData()),
    BIRCH(1500.0, "<gray>BIRCH</gray>", mutableListOf("<gray>There is a slightly miscoloured pixel in</gray>", "<gray>the birch door item texture.</gray>"), ItemRarity.COMMON, Material.BIRCH_WOOD.createBlockData(), Material.STRIPPED_BIRCH_WOOD.createBlockData(), Material.BIRCH_PLANKS.createBlockData()),
    JUNGLE(1500.0, "<gray>JUNGLE</gray>", mutableListOf("<gray>Did someone say George?</gray>"), ItemRarity.COMMON, Material.JUNGLE_WOOD.createBlockData(), Material.STRIPPED_JUNGLE_WOOD.createBlockData(), Material.JUNGLE_PLANKS.createBlockData()),
    ACACIA(1500.0, "<gray>ACACIA</gray>", mutableListOf("<gray>The color of the acacia sapling</gray>", "<gray>is different from the fully grown tree.</gray>"), ItemRarity.COMMON, Material.ACACIA_WOOD.createBlockData(), Material.STRIPPED_ACACIA_WOOD.createBlockData(), Material.ACACIA_PLANKS.createBlockData()),
    DARK_OAK(1500.0, "<gray>DARK OAK</gray>", mutableListOf("<gray>Dark oak was added in 1.7.2</gray>"), ItemRarity.COMMON, Material.DARK_OAK_WOOD.createBlockData(), Material.STRIPPED_DARK_OAK_WOOD.createBlockData(), Material.DARK_OAK_PLANKS.createBlockData()),
    BAMBOO(1500.0, "<gray>BAMBOO</gray>", mutableListOf("<gray>Since when was bamboo wood in minecraft?</gray>"), ItemRarity.COMMON, Material.BAMBOO_BLOCK.createBlockData(), Material.STRIPPED_BAMBOO_BLOCK.createBlockData(), Material.BAMBOO_PLANKS.createBlockData()),
    CHERRY(1500.0, "<gray>CHERRY</gray>", mutableListOf("<gray>Pink wood. Need I say more?</gray>"), ItemRarity.COMMON, Material.CHERRY_WOOD.createBlockData(), Material.STRIPPED_CHERRY_WOOD.createBlockData(), Material.CHERRY_PLANKS.createBlockData()),
    WARPED(1500.0, "<gray>WARPED</gray>", mutableListOf("<gray>Warped forests spawn endermen and striders.</gray>"), ItemRarity.COMMON, Material.WARPED_HYPHAE.createBlockData(), Material.STRIPPED_WARPED_HYPHAE.createBlockData(), Material.WARPED_PLANKS.createBlockData()),
    CRIMSON(1500.0, "<gray>CRIMSON</gray>", mutableListOf("<gray>Why have leaves when you could have</gray>", "<gray>nether wart blocks?</gray>"), ItemRarity.COMMON, Material.CRIMSON_HYPHAE.createBlockData(), Material.STRIPPED_CRIMSON_HYPHAE.createBlockData(), Material.CRIMSON_PLANKS.createBlockData()),


    OCEAN_MONUMENT(2500.0, "<aqua>OCEAN MONUMENT</aqua>", mutableListOf("<gray>Ocean monuments were added in the bountiful update (1.8)</gray>"), ItemRarity.RARE, Material.PRISMARINE.createBlockData(), Material.SEA_LANTERN.createBlockData(), Material.SPONGE.createBlockData()),
    STONE_BRICKS(1500.0, "<gray>STONE BRICKS</gray>", mutableListOf("<gray>Mossy Stone Bricks can be crafted with stone bricks</gray>", "<gray>and a vine or a block of moss</gray>"), ItemRarity.COMMON, Material.STONE_BRICKS.createBlockData(), Material.MOSSY_STONE_BRICKS.createBlockData(), Material.CRACKED_STONE_BRICKS.createBlockData()),

    CHISELED(2500.0, "<aqua>CHISELED</aqua>", mutableListOf("<gray>3 Chiseled blocks</gray>"), ItemRarity.RARE, Material.CHISELED_STONE_BRICKS.createBlockData(), Material.CHISELED_SANDSTONE.createBlockData(), Material.CHISELED_RED_SANDSTONE.createBlockData()),


    BEDROCK(5000.0, "<dark_purple>BEDROCK</dark_purple>", mutableListOf("<gray>The most unbreakable naturally generating Minecraft block.</gray>"), ItemRarity.MYTHIC, Material.BEDROCK.createBlockData(), Material.OBSIDIAN.createBlockData(), Material.PURPLE_STAINED_GLASS.createBlockData()),
    MUSIC(5000.0, "<dark_purple>MUSIC</dark_purple>", mutableListOf("<gray>Can you play a music disc in this?</gray>"), ItemRarity.MYTHIC, Material.JUKEBOX.createBlockData(), Material.NOTE_BLOCK.createBlockData(), Material.DARK_OAK_PLANKS.createBlockData()),

    // halloween
    SCARY(1000.0, "<yellow>SCARY</yellow>", mutableListOf("<gray>OoooOooh! Spooky!</gray>"), ItemRarity.UNCOMMON, Material.ORANGE_CONCRETE.createBlockData(), Material.JACK_O_LANTERN.createBlockData(), Material.PUMPKIN.createBlockData());


    override val stack: ItemStack
        get() = StackBuilder(blocks[0].material).unbreakable(true).amount(64).named(Component.text("").style(Style.style()).append(miniMessage().deserialize(displayName))).lore(*description.map { Component.text("").style(Style.style()).append(miniMessage().deserialize(it)) }.toTypedArray(), Component.text("").style(Style.style()).append(miniMessage().deserialize(rarity.displayName))).build()


}