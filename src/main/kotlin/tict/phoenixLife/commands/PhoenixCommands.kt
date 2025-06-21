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
            "end" -> handleEnd(sender)
            "setmaxtime" -> handleSetMaxTime(sender, args)
            "settime" -> handleSetTime(sender, args)
            "addlives" -> handleAddLives(sender, args)
            "removelives" -> handleRemoveLives(sender, args)
            "setlives" -> handleSetLives(sender, args)
            "glow" -> handleGlow(sender, args)
            "status" -> handleStatus(sender)
            "help" -> sendHelp(sender)
            else -> {
                sender.sendMessage(Component.text("Unknown subcommand. Use /phoenixlife help", NamedTextColor.RED))
                return true
            }
        }
        
        return true
    }
    
    private fun handleStart(sender: CommandSender) {
        if (plugin.gameManager.startGame()) {
            sender.sendMessage(Component.text("Game started successfully!", NamedTextColor.GREEN))
        }
    }
    
    private fun handlePause(sender: CommandSender) {
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            sender.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW))
                .append(Component.text(" to begin a new game.", NamedTextColor.GRAY)))
            return
        }
        
        if (plugin.gameManager.isGamePaused()) {
            sender.sendMessage(Component.text("Game is already paused!", NamedTextColor.YELLOW))
            return
        }
        
        plugin.gameManager.pauseGame()
        sender.sendMessage(Component.text("Game paused successfully!", NamedTextColor.YELLOW))
    }
    
    private fun handleResume(sender: CommandSender) {
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            sender.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW))
                .append(Component.text(" to begin a new game.", NamedTextColor.GRAY)))
            return
        }
        
        if (!plugin.gameManager.isGamePaused()) {
            sender.sendMessage(Component.text("Game is already running!", NamedTextColor.GREEN))
            return
        }
        
        plugin.gameManager.resumeGame()
        sender.sendMessage(Component.text("Game resumed successfully!", NamedTextColor.GREEN))
    }
    
    private fun handleEnd(sender: CommandSender) {
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            sender.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW))
                .append(Component.text(" to begin a new game.", NamedTextColor.GRAY)))
            return
        }
        
        plugin.gameManager.endGame()
        sender.sendMessage(Component.text("Game ended successfully!", NamedTextColor.GOLD))
    }
    
    private fun handleSetMaxTime(sender: CommandSender, args: Array<out String>) {
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
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
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
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
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
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
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
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
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
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
        if (!plugin.configManager.isGameInitialized()) {
            sender.sendMessage(Component.text("No game is currently running!", NamedTextColor.RED))
            return
        }
        
        // Command syntax: glow [on/off] [team]
        // No args: toggle all players
        // One arg (on/off): turn all players on or off
        // Two args (on/off team): turn specific team on or off
        
        when (args.size) {
            1 -> {
                // No arguments after "glow", toggle all players
                if (plugin.server.onlinePlayers.any { it.isGlowing }) {
                    plugin.gameManager.stopGlowing()
                } else {
                    plugin.gameManager.glowAllPlayers()
                }
            }
            
            2 -> {
                // One argument: on/off for all players
                when (args[1].lowercase()) {
                    "on" -> plugin.gameManager.glowAllPlayers()
                    "off" -> plugin.gameManager.stopGlowing()
                    else -> {
                        sender.sendMessage(Component.text("Usage: /phoenixlife glow [on/off] [team]", NamedTextColor.RED))
                        return
                    }
                }
            }
            
            3 -> {
                // Two arguments: on/off for specific team
                val action = args[1].lowercase()
                if (action != "on" && action != "off") {
                    sender.sendMessage(Component.text("Usage: /phoenixlife glow [on/off] [team]", NamedTextColor.RED))
                    return
                }
                
                val color = when (args[2].lowercase()) {
                    "darkgreen", "dark_green" -> LivesManager.NameColor.DARK_GREEN
                    "green" -> LivesManager.NameColor.GREEN
                    "yellow" -> LivesManager.NameColor.YELLOW
                    "red" -> LivesManager.NameColor.RED
                    "gray", "grey" -> LivesManager.NameColor.GRAY
                    else -> {
                        sender.sendMessage(Component.text("Invalid team! Use: darkgreen, green, yellow, red, or gray", NamedTextColor.RED))
                        return
                    }
                }
                
                if (action == "on") {
                    plugin.gameManager.glowPlayersByColor(color)
                } else {
                    plugin.gameManager.stopGlowingByColor(color)
                }
            }
            
            else -> {
                sender.sendMessage(Component.text("Usage: /phoenixlife glow [on/off] [team]", NamedTextColor.RED))
            }
        }
    }
    
    private fun handleStatus(sender: CommandSender) {
        val isInitialized = plugin.configManager.isGameInitialized()
        
        if (!isInitialized) {
            sender.sendMessage(Component.text("=== Phoenix Life Status ===", NamedTextColor.GOLD))
            sender.sendMessage(Component.text("Game State: ", NamedTextColor.WHITE)
                .append(Component.text("Not Initialized", NamedTextColor.DARK_GRAY)))
            sender.sendMessage(Component.text("No game has been started yet.", NamedTextColor.GRAY))
            sender.sendMessage(Component.text("Use ", NamedTextColor.GRAY)
                .append(Component.text("/phoenixlife start", NamedTextColor.YELLOW))
                .append(Component.text(" to begin a game.", NamedTextColor.GRAY)))
            return
        }
        
        val gameState = if (plugin.gameManager.isGamePaused()) "Paused" else "Running"
        val timerFormatted = plugin.roundTimer.getRemainingTimeFormatted()
        val onlinePlayers = plugin.server.onlinePlayers.toList()
        
        sender.sendMessage(Component.text("=== Phoenix Life Status ===", NamedTextColor.GOLD))
        sender.sendMessage(Component.text("Game State: ", NamedTextColor.WHITE)
            .append(Component.text(gameState, if (plugin.gameManager.isGamePaused()) NamedTextColor.RED else NamedTextColor.GREEN))
            .append(Component.text(" | Timer: ", NamedTextColor.WHITE))
            .append(Component.text(timerFormatted, NamedTextColor.YELLOW)))
        
        sender.sendMessage(Component.text("", NamedTextColor.WHITE))
        sender.sendMessage(Component.text("Players (${onlinePlayers.size} online):", NamedTextColor.AQUA))
        
        // Sort players by lives (highest first), then by name
        val sortedPlayers = onlinePlayers.sortedWith(compareByDescending<org.bukkit.entity.Player> { 
            plugin.livesManager.getLives(it) 
        }.thenBy { it.name })
        
        var spectatorCount = 0
        var aliveCount = 0
        
        for (player in sortedPlayers) {
            val lives = plugin.livesManager.getLives(player)
            val color = plugin.livesManager.getColorForLives(lives)
            
            val teamName = when (color) {
                LivesManager.NameColor.DARK_GREEN -> "Dark Green"
                LivesManager.NameColor.GREEN -> "Green"
                LivesManager.NameColor.YELLOW -> "Yellow"
                LivesManager.NameColor.RED -> "Red"
                LivesManager.NameColor.GRAY -> "Gray"
            }
            
            val teamColor = when (color) {
                LivesManager.NameColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
                LivesManager.NameColor.GREEN -> NamedTextColor.GREEN
                LivesManager.NameColor.YELLOW -> NamedTextColor.YELLOW
                LivesManager.NameColor.RED -> NamedTextColor.RED
                LivesManager.NameColor.GRAY -> NamedTextColor.GRAY
            }
            
            if (lives > 0) {
                aliveCount++
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
                val heartsDisplay = "❤".repeat(maxHearts)
                
                sender.sendMessage(Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(teamName, teamColor))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text(player.name, teamColor))
                    .append(Component.text(" (${lives} lives) ", NamedTextColor.WHITE))
                    .append(Component.text(heartsDisplay, NamedTextColor.RED)))
            } else {
                spectatorCount++
                sender.sendMessage(Component.text("[", NamedTextColor.GRAY)
                    .append(Component.text(teamName, teamColor))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text(player.name, teamColor))
                    .append(Component.text(" (0 lives) ", NamedTextColor.WHITE))
                    .append(Component.text("SPECTATOR", NamedTextColor.DARK_RED)))
            }
        }
        
        sender.sendMessage(Component.text("", NamedTextColor.WHITE))
        sender.sendMessage(Component.text("Spectators: ", NamedTextColor.WHITE)
            .append(Component.text(spectatorCount.toString(), NamedTextColor.RED))
            .append(Component.text(" | Alive: ", NamedTextColor.WHITE))
            .append(Component.text(aliveCount.toString(), NamedTextColor.GREEN)))
    }
    
    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Component.text("=== Phoenix Life Admin Commands ===", NamedTextColor.GOLD))
        
        // Game Control Commands
        sender.sendMessage(Component.text("Game Control:", NamedTextColor.AQUA))
        sender.sendMessage(Component.text("/phoenixlife start", NamedTextColor.YELLOW).append(Component.text(" - Start the game (requires 2+ players)", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife pause", NamedTextColor.YELLOW).append(Component.text(" - Pause the game", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife resume", NamedTextColor.YELLOW).append(Component.text(" - Resume the game", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife end", NamedTextColor.YELLOW).append(Component.text(" - End game, backup data, and disable all features", NamedTextColor.WHITE)))
        
        // Timer Commands
        sender.sendMessage(Component.text("Timer Management:", NamedTextColor.AQUA))
        sender.sendMessage(Component.text("/phoenixlife setmaxtime <HH:MM:SS>", NamedTextColor.YELLOW).append(Component.text(" - Set max round time", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife settime <HH:MM:SS>", NamedTextColor.YELLOW).append(Component.text(" - Set remaining time", NamedTextColor.WHITE)))
        
        // Player Management Commands
        sender.sendMessage(Component.text("Player Management:", NamedTextColor.AQUA))
        sender.sendMessage(Component.text("/phoenixlife addlives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Add lives to player", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife removelives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Remove lives from player", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife setlives <player> <amount>", NamedTextColor.YELLOW).append(Component.text(" - Set player's lives", NamedTextColor.WHITE)))
        
        // Visual & Status Commands
        sender.sendMessage(Component.text("Visual & Status:", NamedTextColor.AQUA))
        sender.sendMessage(Component.text("/phoenixlife glow [on/off] [team]", NamedTextColor.YELLOW).append(Component.text(" - Toggle glow for teams", NamedTextColor.WHITE)))
        sender.sendMessage(Component.text("/phoenixlife status", NamedTextColor.YELLOW).append(Component.text(" - View all players and life counts", NamedTextColor.WHITE)))
        
        // Player Commands Info
        sender.sendMessage(Component.text("", NamedTextColor.WHITE))
        sender.sendMessage(Component.text("Player Commands:", NamedTextColor.GREEN))
        sender.sendMessage(Component.text("/lives", NamedTextColor.YELLOW).append(Component.text(" - Players can check their own lives", NamedTextColor.WHITE)))
        
        // Teams Info
        sender.sendMessage(Component.text("", NamedTextColor.WHITE))
        sender.sendMessage(Component.text("Teams: ", NamedTextColor.WHITE)
            .append(Component.text("Dark Green ", NamedTextColor.DARK_GREEN))
            .append(Component.text("(10-7) ", NamedTextColor.WHITE))
            .append(Component.text("Green ", NamedTextColor.GREEN))
            .append(Component.text("(6-5) ", NamedTextColor.WHITE))
            .append(Component.text("Yellow ", NamedTextColor.YELLOW))
            .append(Component.text("(4-3) ", NamedTextColor.WHITE))
            .append(Component.text("Red ", NamedTextColor.RED))
            .append(Component.text("(2-1) ", NamedTextColor.WHITE))
            .append(Component.text("Gray ", NamedTextColor.GRAY))
            .append(Component.text("(0)", NamedTextColor.WHITE)))
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (!sender.hasPermission("phoenixlife.admin")) {
            return emptyList()
        }
        
        return when (args.size) {
            1 -> listOf("start", "pause", "resume", "end", "setmaxtime", "settime", "addlives", "removelives", "setlives", "glow", "status", "help")
                .filter { it.startsWith(args[0].lowercase()) }
            
            2 -> when (args[0].lowercase()) {
                "addlives", "removelives", "setlives" -> plugin.server.onlinePlayers.map { it.name }
                    .filter { it.startsWith(args[1]) }
                "glow" -> listOf("on", "off")
                    .filter { it.startsWith(args[1].lowercase()) }
                else -> emptyList()
            }
            
            3 -> when (args[0].lowercase()) {
                "addlives", "removelives", "setlives" -> listOf("1", "2", "3", "5", "10")
                "glow" -> if (args[1].lowercase() in listOf("on", "off")) {
                    listOf("darkgreen", "green", "yellow", "red", "gray")
                        .filter { it.startsWith(args[2].lowercase()) }
                } else emptyList()
                else -> emptyList()
            }
            
            else -> emptyList()
        }
    }
}