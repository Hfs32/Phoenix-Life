package tict.phoenixLife.commands

import org.bukkit.ChatColor
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
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command!")
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
                sender.sendMessage("${ChatColor.RED}Unknown subcommand. Use /phoenixlife help")
                return true
            }
        }
        
        return true
    }
    
    private fun handleStart(sender: CommandSender) {
        plugin.gameManager.startGame()
        sender.sendMessage("${ChatColor.GREEN}Game started successfully!")
    }
    
    private fun handlePause(sender: CommandSender) {
        plugin.gameManager.pauseGame()
        sender.sendMessage("${ChatColor.YELLOW}Game paused successfully!")
    }
    
    private fun handleResume(sender: CommandSender) {
        plugin.gameManager.resumeGame()
        sender.sendMessage("${ChatColor.GREEN}Game resumed successfully!")
    }
    
    private fun handleSetMaxTime(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife setmaxtime <HH:MM:SS>")
            return
        }
        
        val timeString = args[1]
        val timeParts = timeString.split(":")
        
        if (timeParts.size != 3) {
            sender.sendMessage("${ChatColor.RED}Time must be in format HH:MM:SS")
            return
        }
        
        try {
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()
            
            val totalMillis = (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
            plugin.roundTimer.setMaxDuration(totalMillis)
            
            sender.sendMessage("${ChatColor.GREEN}Max round time set to $timeString")
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Invalid time format!")
        }
    }
    
    private fun handleSetTime(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife settime <HH:MM:SS>")
            return
        }
        
        val timeString = args[1]
        val timeParts = timeString.split(":")
        
        if (timeParts.size != 3) {
            sender.sendMessage("${ChatColor.RED}Time must be in format HH:MM:SS")
            return
        }
        
        try {
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()
            
            val totalMillis = (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L)
            plugin.roundTimer.setRemainingTime(totalMillis)
            
            sender.sendMessage("${ChatColor.GREEN}Remaining time set to $timeString")
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Invalid time format!")
        }
    }
    
    private fun handleAddLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife addlives <player> <amount>")
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage("${ChatColor.RED}Player not found!")
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.addLives(target, amount)
            sender.sendMessage("${ChatColor.GREEN}Added $amount lives to ${target.name}")
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Amount must be a number!")
        }
    }
    
    private fun handleRemoveLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife removelives <player> <amount>")
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage("${ChatColor.RED}Player not found!")
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.removeLives(target, amount)
            sender.sendMessage("${ChatColor.GREEN}Removed $amount lives from ${target.name}")
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Amount must be a number!")
        }
    }
    
    private fun handleSetLives(sender: CommandSender, args: Array<out String>) {
        if (args.size < 3) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife setlives <player> <amount>")
            return
        }
        
        val target = plugin.server.getPlayer(args[1])
        if (target == null) {
            sender.sendMessage("${ChatColor.RED}Player not found!")
            return
        }
        
        try {
            val amount = args[2].toInt()
            plugin.livesManager.setLives(target, amount)
            sender.sendMessage("${ChatColor.GREEN}Set ${target.name}'s lives to $amount")
        } catch (e: NumberFormatException) {
            sender.sendMessage("${ChatColor.RED}Amount must be a number!")
        }
    }
    
    private fun handleGlow(sender: CommandSender, args: Array<out String>) {
        if (args.size < 2) {
            sender.sendMessage("${ChatColor.RED}Usage: /phoenixlife glow <darkgreen|green|yellow|red|gray>")
            return
        }
        
        val color = when (args[1].lowercase()) {
            "darkgreen", "dark_green" -> LivesManager.NameColor.DARK_GREEN
            "green" -> LivesManager.NameColor.GREEN
            "yellow" -> LivesManager.NameColor.YELLOW
            "red" -> LivesManager.NameColor.RED
            "gray", "grey" -> LivesManager.NameColor.GRAY
            else -> {
                sender.sendMessage("${ChatColor.RED}Invalid color! Use: darkgreen, green, yellow, red, or gray")
                return
            }
        }
        
        plugin.gameManager.glowPlayersByColor(color)
    }
    
    private fun handleStopGlow(sender: CommandSender) {
        plugin.gameManager.stopGlowing()
    }
    
    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage("${ChatColor.GOLD}=== Phoenix Life Commands ===")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife start ${ChatColor.WHITE}- Start the game")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife pause ${ChatColor.WHITE}- Pause the game")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife resume ${ChatColor.WHITE}- Resume the game")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife setmaxtime <HH:MM:SS> ${ChatColor.WHITE}- Set max round time")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife settime <HH:MM:SS> ${ChatColor.WHITE}- Set remaining time")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife addlives <player> <amount> ${ChatColor.WHITE}- Add lives")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife removelives <player> <amount> ${ChatColor.WHITE}- Remove lives")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife setlives <player> <amount> ${ChatColor.WHITE}- Set lives")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife glow <color> ${ChatColor.WHITE}- Make players glow by color")
        sender.sendMessage("${ChatColor.YELLOW}/phoenixlife stopglow ${ChatColor.WHITE}- Stop all glowing")
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