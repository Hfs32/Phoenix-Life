package tict.phoenixLife.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import tict.phoenixLife.PhoenixLife

class PlayerDeathListener(private val plugin: PhoenixLife) : Listener {
    
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        plugin.gameManager.handlePlayerDeath(player)
    }
}