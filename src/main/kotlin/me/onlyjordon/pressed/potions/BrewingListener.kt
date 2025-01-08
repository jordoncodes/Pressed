package me.onlyjordon.pressed.potions

import me.onlyjordon.pressed.stats.UserManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class BrewingListener : Listener {
    @EventHandler
    fun onBrewingClick(e: PlayerInteractEvent) {
        if (e.clickedBlock?.type == Material.BREWING_STAND) {
            e.isCancelled = true
            BrewingGui(UserManager.getUser(e.player.uniqueId)).open()
        }
    }
}