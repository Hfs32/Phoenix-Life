package tict.phoenixLife.scoreboard

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class ScoreboardManager(private val plugin: PhoenixLife) {
    
    private var updateTask: BukkitTask? = null
    private lateinit var scoreboard: Scoreboard
    private lateinit var objective: Objective
    private val teams = mutableMapOf<LivesManager.NameColor, Team>()
    
    init {
        setupScoreboard()
    }
    
    private fun setupScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().newScoreboard
        objective = scoreboard.registerNewObjective("phoenixlife", "dummy", "${ChatColor.GOLD}${ChatColor.BOLD}Phoenix Life")
        objective.displaySlot = DisplaySlot.SIDEBAR
        
        // Create teams for each color group (for sorting)
        createTeam(LivesManager.NameColor.DARK_GREEN, "1_darkgreen", ChatColor.DARK_GREEN)
        createTeam(LivesManager.NameColor.GREEN, "2_green", ChatColor.GREEN)
        createTeam(LivesManager.NameColor.YELLOW, "3_yellow", ChatColor.YELLOW)
        createTeam(LivesManager.NameColor.RED, "4_red", ChatColor.RED)
        createTeam(LivesManager.NameColor.GRAY, "5_gray", ChatColor.GRAY)
    }
    
    private fun createTeam(color: LivesManager.NameColor, name: String, chatColor: ChatColor) {
        val team = scoreboard.registerNewTeam(name)
        team.color = chatColor
        team.prefix = "$chatColor"
        teams[color] = team
    }
    
    fun startUpdating() {
        updateTask = plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            updateScoreboard()
        }, 0L, 20L) // Update every second
    }
    
    fun stopUpdating() {
        updateTask?.cancel()
        updateTask = null
    }
    
    private fun updateScoreboard() {
        // Clear old scores
        scoreboard.entries.forEach { entry ->
            scoreboard.resetScores(entry)
        }
        
        // Clear team entries
        teams.values.forEach { team ->
            team.entries.forEach { entry ->
                team.removeEntry(entry)
            }
        }
        
        // Update timer display
        val timerFormatted = plugin.roundTimer.getRemainingTimeFormatted()
        val timerText = "${ChatColor.WHITE}Timer: ${ChatColor.AQUA}$timerFormatted"
        objective.getScore(timerText).score = 100
        
        // Add separator
        objective.getScore("${ChatColor.STRIKETHROUGH}               ").score = 99
        
        // Update game status
        val gameStatus = if (plugin.gameManager.isGamePaused()) {
            "${ChatColor.YELLOW}Game: ${ChatColor.RED}PAUSED"
        } else {
            "${ChatColor.YELLOW}Game: ${ChatColor.GREEN}ACTIVE"
        }
        objective.getScore(gameStatus).score = 98
        
        // Add another separator
        objective.getScore(" ").score = 97
        
        // Update player list
        var score = 96
        plugin.server.onlinePlayers
            .sortedWith(compareBy(
                { player -> getColorPriority(plugin.livesManager.getColorForLives(plugin.livesManager.getLives(player))) },
                { player -> player.name }
            ))
            .forEach { player ->
                val lives = plugin.livesManager.getLives(player)
                val color = plugin.livesManager.getColorForLives(lives)
                val team = teams[color]
                
                // Add player to appropriate team for tab list coloring
                team?.addEntry(player.name)
                
                // Create scoreboard entry
                val displayText = "${getColorForDisplay(color)}${player.name}: ${ChatColor.WHITE}$lives lives"
                objective.getScore(displayText).score = score--
                
                // Apply scoreboard to player
                player.scoreboard = scoreboard
            }
    }
    
    private fun getColorPriority(color: LivesManager.NameColor): Int {
        return when (color) {
            LivesManager.NameColor.DARK_GREEN -> 0
            LivesManager.NameColor.GREEN -> 1
            LivesManager.NameColor.YELLOW -> 2
            LivesManager.NameColor.RED -> 3
            LivesManager.NameColor.GRAY -> 4
        }
    }
    
    private fun getColorForDisplay(color: LivesManager.NameColor): ChatColor {
        return when (color) {
            LivesManager.NameColor.DARK_GREEN -> ChatColor.DARK_GREEN
            LivesManager.NameColor.GREEN -> ChatColor.GREEN
            LivesManager.NameColor.YELLOW -> ChatColor.YELLOW
            LivesManager.NameColor.RED -> ChatColor.RED
            LivesManager.NameColor.GRAY -> ChatColor.GRAY
        }
    }
}