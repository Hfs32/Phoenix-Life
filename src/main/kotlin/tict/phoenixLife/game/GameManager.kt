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
        
        plugin.server.broadcastMessage("${ChatColor.GREEN}The Phoenix Life game has started!")
        plugin.server.broadcastMessage("${ChatColor.YELLOW}Each player starts with 10 lives. Good luck!")
    }
    
    fun pauseGame() {
        plugin.configManager.setGamePaused(true)
        plugin.roundTimer.pause()
        
        plugin.server.broadcastMessage("${ChatColor.YELLOW}The game has been paused. Deaths will not deduct lives.")
    }
    
    fun resumeGame() {
        plugin.configManager.setGamePaused(false)
        plugin.roundTimer.resume()
        
        plugin.server.broadcastMessage("${ChatColor.GREEN}The game has been resumed. Deaths will deduct lives again.")
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
        
        plugin.server.broadcastMessage("${ChatColor.AQUA}All ${colorName} players are now glowing!")
    }
    
    fun stopGlowing() {
        plugin.server.onlinePlayers.forEach { player ->
            player.isGlowing = false
        }
        plugin.server.broadcastMessage("${ChatColor.AQUA}All players stopped glowing.")
    }
}