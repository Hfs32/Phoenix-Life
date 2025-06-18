package tict.phoenixLife.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import tict.phoenixLife.PhoenixLife
import tict.phoenixLife.lives.LivesManager

class PhoenixCommands(private val plugin: PhoenixLife) : CommandExecutor, TabCompleter {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("phoenixlife.admin")) {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED))
            return true
        }
        
        if (args.isEmpty()) {
            sendHelp(sender)
            return true
        }
        
        when (args[0].lowercase()) {
            "start" -> handleStart(sender)
            "pause" -> handlePause(sender)
            "resume" -> handleResume(sender)
            "setmaxtime" -> handleSetMaxTime(sender, args)
            "settime" -> handleSetTime(sender, args)
            "addlives" -> handleAddLives(sender, args)
            "removelives" -> handleRemoveLives(sender, args)
            "setlives" -> handleSetLives(sender, args)
            "glow" -> handleGlow(sender, args)
            "stopglow" -> handleStopGlow(sender)
            "help" -> sendHelp(sender)
            else -> {
                sender.sendMessage(Component.text("Unknown subcommand. Use /phoenixlife help", NamedTextColor.RED))
                return true
            }
        }
        
        return true
    }
    
    private fun handleStart(sender: CommandSender) {
        plugin.gameManager.startGame()
        sender.sendMessage(Component.text("Game started successfully!", NamedTextColor.GREEN))
    }
    
    private fun handlePause(sender: CommandSender) {
        plugin.gameManager.pauseGame()
        sender.sendMessage(Component.text("Game paused successfully!", NamedTextColor.YELLOW))
    }
    
    private fun handleResume(sender: CommandSender) {
        plugin.gameManager.resumeGame()
        sender.sendMessage(Component.text("Game resumed successfully!", NamedTextColor.GREEN))
    }
    
    private fun handleSetMaxTime(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage(Component.text("Usage: /phoenixlife setmaxtime <HH:MM:SS>", NamedTextColor.RED))
            return
        }
        
        val timeString = args[1]
        val timeParts = timeString.split(":")
        
        if (timeParts.size != 3) {
            sender.sendMessage(Component.text("Time must be in format HH:MM:SS", NamedTextColor.RED))
            return
        }
        
        try {
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()
            
            val totalMillis = (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
            plugin.roundTimer.setMaxDuration(totalMillis)
            
            sender.sendMessage(Component.text("Max round time set to $timeString", NamedTextColor.GREEN))
        } catch (e: NumberFormatException) {
            sender.sendMessage(Component.text("Invalid time format!", NamedTextColor.RED))
        }
    }
    
    private fun handleSetTime(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage(Component.text("Usage: /phoenixlife settime <HH:MM:SS>", NamedTextColor.RED))
            return
        }
        
        val timeString = args[1]
        val timeParts = timeString.split(":")
        
        if (timeParts.size != 3) {
            sender.sendMessage(Component.text("Time must be in format HH:MM:SS", NamedTextColor.RED))
            return
        }
        
        try {
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()
            
            val totalMillis = (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
            plugin.roundTimer.setRemainingTime(totalMillis)
            
            sender.sendMessage(Component.text("Remaining time set to $timeString", NamedTextColor.GREEN))
        } catch (e: NumberFormatException) {
            sender.sendMessage(Component.text("Invalid time format!", NamedTextColor.RED))
        }
    }
    
    private fun handleAddLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage(Component.text("Usage: /phoenixlife addlives <player> <amount>", NamedTextColor.RED))
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED))
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.addLives(target, amount)
            sender.sendMessage(Component.text("Added $amount lives to ${target.name}", NamedTextColor.GREEN))
        } catch (e: NumberFormatException) {
            sender.sendMessage(Component.text("Amount must be a number!", NamedTextColor.RED))
        }
    }
    
    private fun handleRemoveLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage(Component.text("Usage: /phoenixlife removelives <player> <amount>", NamedTextColor.RED))
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED))
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.removeLives(target, amount)
            sender.sendMessage(Component.text("Removed $amount lives from ${target.name}", NamedTextColor.GREEN))
        } catch (e: NumberFormatException) {
            sender.sendMessage(Component.text("Amount must be a number!", NamedTextColor.RED))
        }
    }
    
    private fun handleSetLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage(Component.text("Usage: /phoenixlife setlives <player> <amount>", NamedTextColor.RED))
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED))
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.setLives(target, amount)
            sender.sendMessage(Component.text("Set ${target.name}'s lives to $amount", NamedTextColor.GREEN))
        } catch (e: NumberFormatException) {
            sender.sendMessage(Component.text("Amount must be a number!", NamedTextColor.RED))
        }
    }
    
    private fun handleGlow(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage(Component.text("Usage: /phoenixlife glow <darkgreen|green|yellow|red|gray>", NamedTextColor.RED))
            return
        }
        
        val color = when (args[1].lowercase()) {
            "darkgreen", "dark_green" -> LivesManager.NameColor.DARK_GREEN
            "green" -> LivesManager.NameColor.GREEN
            "yellow" -> LivesManager.NameColor.YELLOW
            "red" -> LivesManager.NameColor.RED
            "gray", "grey" -> LivesManager.NameColor.GRAY
            else -> {
                sender.sendMessage(Component.text("Invalid color! Use: darkgreen, green, yellow, red, or gray", NamedTextColor.RED))
                return
            }
        }
        
        plugin.gameManager.glowPlayersByColor(color)
    }
    
    private fun handleStopGlow(sender: CommandSender) {
        plugin.gameManager.stopGlowing()
    }
    
    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Component.text("=== Phoenix Life Commands ===", NamedTextColor.GOLD))
        sender.sendMessage(Component.text("/phoenixlife start", NamedTextColor.YELLOW).append(Component.text(" - Start the game", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife pause", NamedTextColor.YELLOW).append(Component.text(" - Pause the game", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife resume", NamedTextColor.YELLOW).append(Component.text(" - Resume the game", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife setmaxtime <HH:MM:SS>", NamedTextColor.YELLOW).append(Component.text(" - Set max round time", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife settime <HH:MM:SS>", NamedTextColor.YELLOW).append(Component.text(" - Set remaining time", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife addlives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Add lives", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife removelives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Remove lives", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife setlives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Set lives", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife glow <color>", NamedTextColor.YELLOW).append(Component.text(" - Make players glow by color", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife stopglow", NamedTextColor.YELLOW).append(Component.text(" - Stop all glowing", NamedTextColor.WHITE)))
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (!sender.hasPermission("phoenixlife.admin")) {
            return emptyList()
        }
        
        return when (args.size) {
            1 -> listOf("start", "pause", "resume", "setmaxtime", "settime", "addlives", "removelives", "setlives", "glow", "stopglow", "help")
                .filter { it.startsWith(args[0].lowercase()) }
            
            2 -> when (args[0].lowercase()) {
                "addlives", "removelives", "setlives" -> plugin.server.onlinePlayers.map { it.name }
                    .filter { it.startsWith(args[1]) }
                "glow" -> listOf("darkgreen", "green", "yellow", "red", "gray")
                    .filter { it.startsWith(args[1].lowercase()) }
                else -> emptyList()
            }
            
            3 -> when (args[0].lowercase()) {
                "addlives", "removelives", "setlives" -> listOf("1", "2", "3", "5", "10")
                else -> emptyList()
            }
            
            else -> emptyList()
        }
    }
}