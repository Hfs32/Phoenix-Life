package tict.phoenixLife.config

import org.bukkit.configuration.file.YamlConfiguration
import tict.phoenixLife.PhoenixLife
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class ConfigManager(private val plugin: PhoenixLife) {
    
    private lateinit var dataFile: File
    private lateinit var dataConfig: YamlConfiguration
    
    init {
        plugin.dataFolder.mkdirs()
        dataFile = File(plugin.dataFolder, "data.yml")
        dataConfig = YamlConfiguration.loadConfiguration(dataFile)
    }
    
    fun loadConfig() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        
        if (!dataFile.exists()) {
            dataFile.createNewFile()
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile)
    }
    
    fun saveConfig() {
        try {
            dataConfig.save(dataFile)
        } catch (e: Exception) {
            plugin.logger.severe("Could not save data.yml: ${e.message}")
        }
    }
    
    // Lives management
    fun getLives(uuid: UUID): Int {
        return dataConfig.getInt("players.${uuid}.lives", 10)
    }
    
    fun setLives(uuid: UUID, lives: Int) {
        dataConfig.set("players.${uuid}.lives", lives.coerceIn(0, 10))
        saveConfig()
    }
    
    // Game state management
    fun isGamePaused(): Boolean {
        return dataConfig.getBoolean("game.paused", true)
    }
    
    fun setGamePaused(paused: Boolean) {
        dataConfig.set("game.paused", paused)
        saveConfig()
    }
    
    fun isGameInitialized(): Boolean {
        return dataConfig.getBoolean("game.initialized", false)
    }
    
    fun setGameInitialized(initialized: Boolean) {
        dataConfig.set("game.initialized", initialized)
        saveConfig()
    }
    
    // Timer management
    fun getRoundDuration(): Long {
        return dataConfig.getLong("timer.duration", 10800000) // Default 3 hours in milliseconds
    }
    
    fun setRoundDuration(duration: Long) {
        dataConfig.set("timer.duration", duration)
        saveConfig()
    }
    
    fun getRemainingTime(): Long {
        return dataConfig.getLong("timer.remaining", getRoundDuration())
    }
    
    fun setRemainingTime(remaining: Long) {
        dataConfig.set("timer.remaining", remaining)
        saveConfig()
    }
    
    fun getTimerStartTime(): Long {
        return dataConfig.getLong("timer.startTime", 0)
    }
    
    fun setTimerStartTime(startTime: Long) {
        dataConfig.set("timer.startTime", startTime)
        saveConfig()
    }
    
    fun createBackup(): Boolean {
        return try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
            val backupFile = File(plugin.dataFolder, "data_backup_$timestamp.yml")
            dataFile.copyTo(backupFile, overwrite = false)
            plugin.logger.info("Created backup: ${backupFile.name}")
            true
        } catch (e: Exception) {
            plugin.logger.severe("Failed to create backup: ${e.message}")
            false
        }
    }
    
    fun resetAllPlayerData() {
        dataConfig.getConfigurationSection("players")?.getKeys(false)?.forEach { uuidString ->
            dataConfig.set("players.$uuidString", null)
        }
        saveConfig()
    }
}