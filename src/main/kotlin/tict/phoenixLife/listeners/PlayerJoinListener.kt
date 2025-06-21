package tict.phoenixLife.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class PlayerJoinListener(private val plugin: PhoenixLife) : Listener {
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        // Only apply game effects if game has been initialized
        if (plugin.configManager.isGameInitialized()) {
            // Initialize player if new
            val lives = plugin.livesManager.getLives(player)
            
            // Apply current lives state (hearts, color, game mode)
            plugin.livesManager.setLives(player, lives)
            
            // Send welcome message with current status
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                sendWelcomeMessage(player, lives)
            }, 20L) // Wait 1 second after join
        } else {
            // Game not initialized, send vanilla welcome
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                player.sendMessage(Component.text("Welcome to the server!", NamedTextColor.GREEN))
                player.sendMessage(Component.text("Phoenix-Life is installed but no game is active.", NamedTextColor.GRAY))
                player.sendMessage(Component.text("Ask an admin to start a game with ", NamedTextColor.GRAY)
                    .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW)))
            }, 20L)
        }
    }
    
    private fun sendWelcomeMessage(player: org.bukkit.entity.Player, lives: Int) {
        val color = plugin.livesManager.getColorForLives(lives)
        val teamName = when (color) {
            LivesManager.NameColor.DARK_GREEN -> "Dark Green"
            LivesManager.NameColor.GREEN -> "Green"
            LivesManager.NameColor.YELLOW -> "Yellow"
            LivesManager.NameColor.RED -> "Red"
            LivesManager.NameColor.GRAY -> "Gray"
        }
        
        val teamColor = when (color) {
            LivesManager.NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
            LivesManager.NameColor.GREEN -> NamedTextColor.GREEN
            LivesManager.NameColor.YELLOW -> NamedTextColor.YELLOW
            LivesManager.NameColor.RED -> NamedTextColor.RED
            LivesManager.NameColor.GRAY -> NamedTextColor.GRAY
        }
        
        if (lives > 0) {
            player.sendMessage(Component.text("Welcome back to Phoenix Life!", NamedTextColor.GOLD))
            player.sendMessage(Component.text("You have ", NamedTextColor.WHITE)
                .append(Component.text("${lives}/10 lives", teamColor))
                .append(Component.text(" remaining on team ", NamedTextColor.WHITE))
                .append(Component.text(teamName, teamColor))
                .append(Component.text(".", NamedTextColor.WHITE)))
            player.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/lives", NamedTextColor.YELLOW))
                .append(Component.text(" to check your status anytime.", NamedTextColor.GRAY)))
        } else {
            player.sendMessage(Component.text("Welcome back to Phoenix Life!", NamedTextColor.GOLD))
            player.sendMessage(Component.text("You are currently eliminated and in spectator mode.", NamedTextColor.GRAY))
            player.sendMessage(Component.text("Wait for the next game to start!", NamedTextColor.YELLOW))
        }
    }
}