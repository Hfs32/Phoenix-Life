package tict.phoenixLife.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class PlayerCommands(private val plugin: PhoenixLife) : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED))
            return true
        }
        
        when (command.name.lowercase()) {
            "lives" -> handleLivesCommand(sender)
        }
        
        return true
    }
    
    private fun handleLivesCommand(player: Player) {
        // Check if game is initialized
        if (!plugin.configManager.isGameInitialized()) {
            player.sendMessage(Component.text("No Phoenix Life game is currently active.", NamedTextColor.GRAY))
            player.sendMessage(Component.text("Ask an admin to start a game with ", NamedTextColor.GRAY)
                .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW)))
            return
        }
        
        val lives = plugin.livesManager.getLives(player)
        val color = plugin.livesManager.getColorForLives(lives)
        
        val colorName = when (color) {
            LivesManager.NameColor.DARK_GREEN -> "Dark Green"
            LivesManager.NameColor.GREEN -> "Green"
            LivesManager.NameColor.YELLOW -> "Yellow"
            LivesManager.NameColor.RED -> "Red"
            LivesManager.NameColor.GRAY -> "Gray"
        }
        
        val namedTextColor = when (color) {
            LivesManager.NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
            LivesManager.NameColor.GREEN -> NamedTextColor.GREEN
            LivesManager.NameColor.YELLOW -> NamedTextColor.YELLOW
            LivesManager.NameColor.RED -> NamedTextColor.RED
            LivesManager.NameColor.GRAY -> NamedTextColor.GRAY
        }
        
        // Calculate max hearts display
        val maxHearts = when (lives) {
            10 -> 1
            9 -> 2
            8 -> 3
            7 -> 4
            6 -> 5
            5 -> 6
            4 -> 7
            3 -> 8
            2 -> 9
            1 -> 10
            else -> 10
        }
        
        // Build hearts display
        val heartsDisplay = "❤".repeat(maxHearts)
        
        if (lives > 0) {
            player.sendMessage(Component.text("You have ", NamedTextColor.WHITE)
                .append(Component.text("${lives}/10 lives", namedTextColor))
                .append(Component.text(" remaining.", NamedTextColor.WHITE)))
            
            player.sendMessage(Component.text("Team: ", NamedTextColor.WHITE)
                .append(Component.text(colorName, namedTextColor)))
        } else {
            player.sendMessage(Component.text("You are eliminated and in spectator mode.", NamedTextColor.GRAY))
            player.sendMessage(Component.text("Wait for the next game to start!", NamedTextColor.YELLOW))
        }
    }
}