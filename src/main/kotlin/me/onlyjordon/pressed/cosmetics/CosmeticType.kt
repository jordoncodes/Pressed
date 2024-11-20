package me.onlyjordon.pressed.cosmetics

import org.bukkit.inventory.ItemStack

interface CosmeticType {
    val rarity: ItemRarity
    val price: Double
    val displayName: String
    val description: MutableList<String>
    val stack: ItemStack

    companion object {
        fun <T> getRandomItemWithProbabilities(enum: Class<out Enum<out T>>): T where T: Enum<T>, T : CosmeticType {
            val listOfItems = ArrayList<CosmeticType>()
            enum.enumConstants.filter { it != BlockType.DEFAULT && it != HelmetType.DEFAULT && it != StickType.DEFAULT }.forEach {
                for (i in 0 until (it as T).rarity.chance) {
                    listOfItems.add(it)
                }
            }
            val i = listOfItems.random()
            listOfItems.clear()
            return i as T
        }
    }
}