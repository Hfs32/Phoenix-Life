package tict.phoenixLife

import org.bukkit.plugin.java.JavaPlugin
import tict.phoenixLife.config.ConfigManager
import tict.phoenixLife.game.GameManager
import tict.phoenixLife.commands.PhoenixCommands
import tict.phoenixLife.commands.PlayerCommands
import tict.phoenixLife.listeners.PlayerDeathListener
import tict.phoenixLife.listeners.PlayerJoinListener
import tict.phoenixLife.lives.LivesManager
import tict.phoenixLife.timer.RoundTimer
import tict.phoenixLife.scoreboard.ScoreboardManager

class PhoenixLife : JavaPlugin() {
    
    lateinit var configManager: ConfigManager
    lateinit var livesManager: LivesManager
    lateinit var gameManager: GameManager
    lateinit var roundTimer: RoundTimer
    lateinit var scoreboardManager: ScoreboardManager
    
    override fun onEnable() {
        // Initialize managers
        configManager = ConfigManager(this)
        livesManager = LivesManager(this)
        gameManager = GameManager(this)
        roundTimer = RoundTimer(this)
        scoreboardManager = ScoreboardManager(this)
        
        // Register event listeners
        server.pluginManager.registerEvents(PlayerDeathListener(this), this)
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        
        // Register commands
        getCommand("phoenixlife")?.setExecutor(PhoenixCommands(this))
        getCommand("lives")?.setExecutor(PlayerCommands(this))
        
        // Load configurations
        configManager.loadConfig()
        
        // Only start scoreboard updates if game has been initialized
        if (configManager.isGameInitialized()) {
            scoreboardManager.startUpdating()
            logger.info("Phoenix-Life has been enabled! Game is active.")
        } else {
            logger.info("Phoenix-Life has been enabled! Waiting for game to start.")
        }
    }

    override fun onDisable() {
        // Save all data
        configManager.saveConfig()
        roundTimer.stop()
        scoreboardManager.stopUpdating()
        
        logger.info("Phoenix-Life has been disabled!")
    }
}
