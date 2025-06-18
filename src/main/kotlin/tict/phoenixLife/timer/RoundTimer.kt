package tict.phoenixLife.timer

import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import tict.phoenixLife.PhoenixLife
import java.util.concurrent.TimeUnit

class RoundTimer(private val plugin: PhoenixLife) {
    
    private var timerTask: BukkitTask? = null
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    
    fun start() {
        stop() // Cancel any existing timer
        
        startTime = System.currentTimeMillis()
        plugin.configManager.setTimerStartTime(startTime)
        plugin.configManager.setRemainingTime(plugin.configManager.getRoundDuration())
        
        timerTask = object : BukkitRunnable() {
            override fun run() {
                updateTimer()
            }
        }.runTaskTimer(plugin, 0L, 20L) // Run every second
    }
    
    fun pause() {
        pausedTime = System.currentTimeMillis()
        val elapsed = pausedTime - startTime
        val remaining = plugin.configManager.getRoundDuration() - elapsed
        plugin.configManager.setRemainingTime(remaining.coerceAtLeast(0))
        
        timerTask?.cancel()
        timerTask = null
    }
    
    fun resume() {
        if (timerTask != null) return // Already running
        
        startTime = System.currentTimeMillis()
        val remaining = plugin.configManager.getRemainingTime()
        
        timerTask = object : BukkitRunnable() {
            override fun run() {
                updateTimer()
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }
    
    fun stop() {
        timerTask?.cancel()
        timerTask = null
    }
    
    fun setMaxDuration(duration: Long) {
        plugin.configManager.setRoundDuration(duration)
        if (isRunning()) {
            // Restart with new duration
            start()
        }
    }
    
    fun setRemainingTime(remaining: Long) {
        plugin.configManager.setRemainingTime(remaining)
        if (isRunning()) {
            startTime = System.currentTimeMillis()
            plugin.configManager.setTimerStartTime(startTime)
        }
    }
    
    fun getRemainingTimeFormatted(): String {
        val remaining = if (isRunning()) {
            val elapsed = System.currentTimeMillis() - startTime
            (plugin.configManager.getRoundDuration() - elapsed).coerceAtLeast(0)
        } else {
            plugin.configManager.getRemainingTime()
        }
        
        val hours = TimeUnit.MILLISECONDS.toHours(remaining)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) % 60
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    private fun updateTimer() {
        val elapsed = System.currentTimeMillis() - startTime
        val remaining = plugin.configManager.getRoundDuration() - elapsed
        
        if (remaining <= 0) {
            // Timer finished
            timerTask?.cancel()
            timerTask = null
            plugin.gameManager.pauseGame()
            plugin.server.broadcastMessage("${ChatColor.RED}Round timer has expired! The game is now paused.")
        } else {
            // Announce time remaining at specific intervals
            val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(remaining)
            when (remainingSeconds) {
                3600L -> announceTime("1 hour")
                1800L -> announceTime("30 minutes")
                600L -> announceTime("10 minutes")
                300L -> announceTime("5 minutes")
                60L -> announceTime("1 minute")
                30L -> announceTime("30 seconds")
                10L -> announceTime("10 seconds")
                in 1L..5L -> announceTime("$remainingSeconds seconds")
            }
        }
    }
    
    private fun announceTime(timeString: String) {
        plugin.server.broadcastMessage("${ChatColor.YELLOW}Time remaining: $timeString")
    }
    
    private fun isRunning(): Boolean {
        return timerTask != null
    }
}