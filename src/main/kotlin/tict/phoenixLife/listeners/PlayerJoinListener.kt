package tict.phoenixLife.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import tict.phoenixLife.PhoenixLife

class PlayerJoinListener(private val plugin: PhoenixLife) : Listener {
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        // Initialize player if new
        val lives = plugin.livesManager.getLives(player)
        
        // Apply current lives state (hearts, color, game mode)
        plugin.livesManager.setLives(player, lives)
    }
}