package me.onlyjordon.pressed.cosmetics

enum class ItemRarity(val displayName: String, val chance: Int) {
    COMMON("<gray><bold>COMMON</bold></gray>", 80),
    UNCOMMON("<yellow><bold>UNCOMMON</bold></yellow>", 45),
    RARE("<aqua><bold>RARE</bold></aqua>", 25),
    EPIC("<light_purple><bold>EPIC</bold></light_purple>", 15),
    LEGENDARY("<gold><bold>LEGENDARY</bold></gold>", 5),
    MYTHIC("<dark_purple><bold>MYTHIC</bold></dark_purple>", 2),
    SPECIAL("<blue><bold>SPECIAL</bold></blue>", 1)
}