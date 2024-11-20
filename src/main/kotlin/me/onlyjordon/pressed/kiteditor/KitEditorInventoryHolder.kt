package me.onlyjordon.pressed.kiteditor

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class KitEditorInventoryHolder: InventoryHolder {

    lateinit var inv: Inventory

    override fun getInventory(): Inventory {
        return inv
    }
}