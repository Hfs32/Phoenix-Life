package tict.phoenixLife.game

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import tict.phoenixLife.PhoenixLife

class GameManager(private val plugin: PhoenixLife) {
    
    fun isGamePaused(): Boolean {
        return plugin.configManager.isGamePaused()
    }
    
    fun startGame() {
        // Reset all players to 10 lives
        plugin.server.onlinePlayers.forEach { player ->
            plugin.livesManager.setLives(player, 10)
            if (player.gameMode == GameMode.SPECTATOR) {
                player.gameMode = GameMode.SURVIVAL
            }
        }
        
        // Unpause the game
        plugin.configManager.setGamePaused(false)
        
        // Start the timer
        plugin.roundTimer.start()
        
        plugin.server.broadcast(net.kyori.adventure.text.Component.text("The Phoenix Life game has started!", net.kyori.adventure.text.format.NamedTextColor.GREEN))
        plugin.server.broadcast(net.kyori.adventure.text.Component.text("Each player starts with 10 lives. Good luck!", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
    }
    
    fun pauseGame() {
        plugin.configManager.setGamePaused(true)
        plugin.roundTimer.pause()
        
        plugin.server.broadcast(net.kyori.adventure.text.Component.text("The game has been paused. Deaths will not deduct lives.", net.kyori.adventure.text.format.NamedTextColor.YELLOW))
    }
    
    fun resumeGame() {
        plugin.configManager.setGamePaused(false)
        
        // Only reset timer if it has run out of time, otherwise just resume
        val remainingTime = plugin.configManager.getRemainingTime()
        if (remainingTime <= 0) {
            plugin.roundTimer.start() // Reset timer to full duration
            plugin.server.broadcast(net.kyori.adventure.text.Component.text("The game has been resumed. Timer has been reset to full duration.", net.kyori.adventure.text.format.NamedTextColor.GREEN))
        } else {
            plugin.roundTimer.resume() // Resume from where it was paused
            plugin.server.broadcast(net.kyori.adventure.text.Component.text("The game has been resumed. Deaths will deduct lives again.", net.kyori.adventure.text.format.NamedTextColor.GREEN))
        }
    }
    
    fun handlePlayerDeath(player: Player) {
        if (!isGamePaused()) {
            val currentLives = plugin.livesManager.getLives(player)
            if (currentLives > 0) {
                plugin.livesManager.removeLives(player, 1)
                val remainingLives = plugin.livesManager.getLives(player)
                
                if (remainingLives > 0) {
                    player.sendMessage("${ChatColor.RED}You died! You have ${remainingLives} lives remaining.")
                }
            }
        }
    }
    
    fun glowPlayersByColor(color: tict.phoenixLife.lives.LivesManager.NameColor) {
        plugin.server.onlinePlayers.forEach { player ->
            val playerLives = plugin.livesManager.getLives(player)
            val playerColor = plugin.livesManager.getColorForLives(playerLives)
            
            player.isGlowing = playerColor == color
        }
        
        val colorName = when (color) {
            tict.phoenixLife.lives.LivesManager.NameColor.DARK_GREEN -> "Dark Green"
            tict.phoenixLife.lives.LivesManager.NameColor.GREEN -> "Green"
            tict.phoenixLife.lives.LivesManager.NameColor.YELLOW -> "Yellow"
            tict.phoenixLife.lives.LivesManager.NameColor.RED -> "Red"
            tict.phoenixLife.lives.LivesManager.NameColor.GRAY -> "Gray"
        }
        
        plugin.server.broadcast(net.kyori.adventure.text.Component.text("All ${colorName} players are now glowing!", net.kyori.adventure.text.format.NamedTextColor.AQUA))
    }
    
    fun stopGlowing() {
        plugin.server.onlinePlayers.forEach { player ->
            player.isGlowing = false
        }
        plugin.server.broadcast(net.kyori.adventure.text.Component.text("All players stopped glowing.", net.kyori.adventure.text.format.NamedTextColor.AQUA))
    }
}