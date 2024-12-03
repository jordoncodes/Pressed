package me.onlyjordon.pressed.quests.daily.gui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class DailyQuestInventoryHolder: InventoryHolder {

    lateinit var inv: Inventory

    override fun getInventory(): Inventory {
        return inv
    }
}