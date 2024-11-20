package me.onlyjordon.pressed.quests.gui

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class QuestInventoryHolder: InventoryHolder {

    lateinit var inv: Inventory

    override fun getInventory(): Inventory {
        return inv
    }
}