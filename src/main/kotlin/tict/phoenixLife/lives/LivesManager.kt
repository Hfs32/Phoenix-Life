package tict.phoenixLife.lives

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import tict.phoenixLife.PhoenixLife
import java.util.UUID

class LivesManager(private val plugin: PhoenixLife) {
    
    fun getLives(player: Player): Int {
        return plugin.configManager.getLives(player.uniqueId)
    }
    
    fun getLives(uuid: UUID): Int {
        return plugin.configManager.getLives(uuid)
    }
    
    fun setLives(player: Player, lives: Int) {
        val clampedLives = lives.coerceIn(0, 10)
        plugin.configManager.setLives(player.uniqueId, clampedLives)
        
        // Update max hearts
        updateMaxHearts(player, clampedLives)
        
        // Update name color
        updateNameColor(player, clampedLives)
        
        // Handle spectator mode
        if (clampedLives == 0) {
            if (player.gameMode != GameMode.SPECTATOR) {
                player.gameMode = GameMode.SPECTATOR
                strikeWithLightning(player)
                plugin.server.broadcast(net.kyori.adventure.text.Component.text("${player.name} has been eliminated!", net.kyori.adventure.text.format.NamedTextColor.RED))
            }
        } else if (player.gameMode == GameMode.SPECTATOR) {
            player.gameMode = GameMode.SURVIVAL
        }
    }
    
    fun addLives(player: Player, amount: Int) {
        setLives(player, getLives(player) + amount)
    }
    
    fun removeLives(player: Player, amount: Int) {
        setLives(player, getLives(player) - amount)
    }
    
    private fun updateMaxHearts(player: Player, lives: Int) {
        val maxHearts = when (lives) {
            10 -> 2.0  // 1 heart = 2 health points
            9 -> 4.0
            8 -> 6.0
            7 -> 8.0
            6 -> 10.0
            5 -> 12.0
            4 -> 14.0
            3 -> 16.0
            2 -> 18.0
            1 -> 20.0
            else -> 20.0 // Spectator keeps default
        }
        
        val attribute = player.getAttribute(Attribute.MAX_HEALTH)
        if (attribute != null) {
            attribute.baseValue = maxHearts
            
            // Ensure current health doesn't exceed max
            if (player.health > maxHearts) {
                player.health = maxHearts
            }
        } else {
            plugin.logger.warning("Could not get MAX_HEALTH attribute for player ${player.name}")
        }
    }
    
    private fun updateNameColor(player: Player, lives: Int) {
        val color = getColorForLives(lives)
        val namedTextColor = when (color) {
            NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
            NameColor.GREEN -> NamedTextColor.GREEN
            NameColor.YELLOW -> NamedTextColor.YELLOW
            NameColor.RED -> NamedTextColor.RED
            NameColor.GRAY -> NamedTextColor.GRAY
        }
        
        player.displayName(Component.text(player.name, namedTextColor))
        player.playerListName(Component.text(player.name, namedTextColor))
    }
    
    fun getColorForLives(lives: Int): NameColor {
        return when (lives) {
            in 7..10 -> NameColor.DARK_GREEN
            in 5..6 -> NameColor.GREEN
            in 3..4 -> NameColor.YELLOW
            in 1..2 -> NameColor.RED
            else -> NameColor.GRAY
        }
    }
    
    private fun strikeWithLightning(player: Player) {
        player.world.strikeLightningEffect(player.location)
    }
    
    enum class NameColor {
        DARK_GREEN,
        GREEN,
        YELLOW,
        RED,
        GRAY
    }
}