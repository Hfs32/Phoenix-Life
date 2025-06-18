package tict.phoenixLife.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.GameMode
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class GameManager(private val plugin: PhoenixLife) {
    
    fun isGamePaused(): Boolean {
        return plugin.configManager.isGamePaused()
    }
    
    fun startGame() {
        // Check if there are at least 2 players
        if (plugin.server.onlinePlayers.size < 2) {
            plugin.server.broadcast(Component.text("Cannot start game with fewer than 2 players!", NamedTextColor.RED))
            return
        }
        
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
        
        plugin.server.broadcast(Component.text("The Phoenix Life game has started!", NamedTextColor.GREEN))
        plugin.server.broadcast(Component.text("Each player starts with 10 lives. Good luck!", NamedTextColor.YELLOW))
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
                    player.sendMessage(Component.text("You died! You have ${remainingLives} lives remaining.", NamedTextColor.RED))
                }
                
                // Check for victory only if timer is running
                if (plugin.roundTimer.isRunning()) {
                    checkForVictory()
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
    
    fun endGame() {
        // Create backup
        if (plugin.configManager.createBackup()) {
            plugin.server.broadcast(Component.text("Game data backed up successfully.", NamedTextColor.GREEN))
        }
        
        // Stop timer
        plugin.roundTimer.stop()
        
        // Reset all player data
        plugin.configManager.resetAllPlayerData()
        
        // Set game to paused
        plugin.configManager.setGamePaused(true)
        
        // Reset all players
        plugin.server.onlinePlayers.forEach { player ->
            if (player.gameMode == GameMode.SPECTATOR) {
                player.gameMode = GameMode.SURVIVAL
            }
            plugin.livesManager.updateNameColor(player, 10)
            player.isGlowing = false
        }
        
        plugin.server.broadcast(Component.text("The Phoenix Life game has ended.", NamedTextColor.GOLD))
    }
    
    private fun checkForVictory() {
        val playersWithLives = getPlayersWithLives()
        
        if (playersWithLives.size == 1) {
            val winner = playersWithLives.first()
            
            // Spawn victory fireworks
            spawnVictoryFireworks(winner)
            
            // Broadcast victory message
            plugin.server.broadcast(Component.text("${winner.name} has won the Phoenix Life game!", NamedTextColor.GOLD))
            
            // Pause the game
            pauseGame()
        }
    }
    
    private fun getPlayersWithLives(): List<Player> {
        return plugin.server.onlinePlayers.filter { player ->
            plugin.livesManager.getLives(player) > 0
        }
    }
    
    private fun spawnVictoryFireworks(winner: Player) {
        repeat(5) {
            plugin.server.scheduler.runTaskLater(plugin, Runnable {
                val fw = winner.world.spawn(winner.location, Firework::class.java)
                val fwm = fw.fireworkMeta
                
                // Random colors
                val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.WHITE)
                val primaryColor = colors.random()
                val fadeColor = colors.random()
                
                // Random effect types
                val types = listOf(
                    FireworkEffect.Type.BALL_LARGE,
                    FireworkEffect.Type.STAR,
                    FireworkEffect.Type.BURST,
                    FireworkEffect.Type.CREEPER
                )
                
                val effect = FireworkEffect.builder()
                    .withColor(primaryColor)
                    .withFade(fadeColor)
                    .with(types.random())
                    .trail(true)
                    .flicker(true)
                    .build()
                
                fwm.addEffect(effect)
                fwm.power = 2
                fw.fireworkMeta = fwm
            }, (it * 10L)) // Stagger fireworks
        }
    }
    
    private fun LivesManager.updateNameColor(player: Player, lives: Int) {
        val color = getColorForLives(lives)
        val namedTextColor = when (color) {
            LivesManager.NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
            LivesManager.NameColor.GREEN -> NamedTextColor.GREEN
            LivesManager.NameColor.YELLOW -> NamedTextColor.YELLOW
            LivesManager.NameColor.RED -> NamedTextColor.RED
            LivesManager.NameColor.GRAY -> NamedTextColor.GRAY
        }
        
        player.displayName(Component.text(player.name, namedTextColor))
        player.playerListName(Component.text(player.name, namedTextColor))
    }
}