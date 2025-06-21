package tict.phoenixLife.scoreboard

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class ScoreboardManager(private val plugin: PhoenixLife) {
    
    private var updateTask: BukkitTask? = null
    private lateinit var scoreboard: Scoreboard
    private val teams = mutableMapOf<LivesManager.NameColor, Team>()
    
    init {
        setupScoreboard()
    }
    
    private fun setupScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().newScoreboard
        
        // Create teams for player name coloring (keep this functionality)
        createTeam(LivesManager.NameColor.DARK_GREEN, "1_darkgreen", NamedTextColor.DARK_GREEN)
        createTeam(LivesManager.NameColor.GREEN, "2_green", NamedTextColor.GREEN)
        createTeam(LivesManager.NameColor.YELLOW, "3_yellow", NamedTextColor.YELLOW)
        createTeam(LivesManager.NameColor.RED, "4_red", NamedTextColor.RED)
        createTeam(LivesManager.NameColor.GRAY, "5_gray", NamedTextColor.GRAY)
    }
    
    private fun createTeam(color: LivesManager.NameColor, name: String, namedTextColor: NamedTextColor) {
        val team = scoreboard.registerNewTeam(name)
        team.color(namedTextColor)
        team.prefix(Component.empty())
        teams[color] = team
    }
    
    fun startUpdating() {
        updateTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            updateTimer()
        }, 0L, 20L) // Update every second
    }
    
    fun stopUpdating() {
        updateTask?.cancel()
        updateTask = null
    }
    
    fun isUpdating(): Boolean {
        return updateTask != null
    }
    
    fun cleanup() {
        // Stop the update task
        stopUpdating()
        
        // Clear all team entries
        teams.values.forEach { team ->
            team.entries.forEach { entry ->
                team.removeEntry(entry)
            }
        }
        
        // Unregister all teams
        teams.values.forEach { team ->
            team.unregister()
        }
        teams.clear()
        
        // Reset all player scoreboards to default
        plugin.server.onlinePlayers.forEach { player ->
            player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        }
        
        // Clear action bars by sending empty component
        plugin.server.onlinePlayers.forEach { player ->
            player.sendActionBar(Component.empty())
        }
    }
    
    private fun updateTimer() {
        // Only update if game is initialized
        if (!plugin.configManager.isGameInitialized()) {
            return
        }
        
        // Update player team assignments for name coloring
        updatePlayerTeams()
        
        // Display timer and lives in action bar
        val timerFormatted = plugin.roundTimer.getRemainingTimeFormatted()
        val timerColor = if (plugin.gameManager.isGamePaused()) {
            NamedTextColor.RED
        } else {
            NamedTextColor.GREEN
        }
        
        // Send personalized action bar to all online players
        plugin.server.onlinePlayers.forEach { player ->
            val lives = plugin.livesManager.getLives(player)
            val color = plugin.livesManager.getColorForLives(lives)
            
            val livesColor = when (color) {
                LivesManager.NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
                LivesManager.NameColor.GREEN -> NamedTextColor.GREEN
                LivesManager.NameColor.YELLOW -> NamedTextColor.YELLOW
                LivesManager.NameColor.RED -> NamedTextColor.RED
                LivesManager.NameColor.GRAY -> NamedTextColor.GRAY
            }
            
            val actionBarComponent = if (lives > 0) {
                Component.text("Timer: ", NamedTextColor.WHITE)
                    .append(Component.text(timerFormatted, timerColor))
                    .append(Component.text(" | Lives: ", NamedTextColor.WHITE))
                    .append(Component.text("${lives}/10", livesColor))
            } else {
                Component.text("Timer: ", NamedTextColor.WHITE)
                    .append(Component.text(timerFormatted, timerColor))
                    .append(Component.text(" | ", NamedTextColor.WHITE))
                    .append(Component.text("SPECTATOR", NamedTextColor.GRAY))
            }
            
            player.sendActionBar(actionBarComponent)
        }
    }
    
    private fun updatePlayerTeams() {
        // Clear team entries
        teams.values.forEach { team ->
            team.entries.forEach { entry ->
                team.removeEntry(entry)
            }
        }
        
        // Update player team assignments for name coloring
        plugin.server.onlinePlayers.forEach { player ->
            val lives = plugin.livesManager.getLives(player)
            val color = plugin.livesManager.getColorForLives(lives)
            val team = teams[color]
            
            // Add player to appropriate team for tab list coloring
            team?.addEntry(player.name)
            
            // Apply scoreboard to player for team colors
            player.scoreboard = scoreboard
        }
    }
    
}