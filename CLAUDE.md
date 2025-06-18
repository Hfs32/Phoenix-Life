# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Phoenix-Life is a Minecraft Paper plugin for version 1.21.4 written in Kotlin. It implements a "lives" system where players start with 10 lives and lose one each time they die. As players lose lives, their maximum hearts increase (inverse correlation), and their name color changes to indicate remaining lives. Players become spectators when they reach 0 lives.

## Build System

This project uses Gradle with Kotlin DSL:
- Build: `./gradlew build` (creates shadowJar automatically)
- Run server: `./gradlew runServer` (starts Paper 1.21 server with plugin loaded)
- Clean build: `./gradlew clean build`

## Architecture

**Core Components:**
- Main plugin class: `tict.phoenixLife.PhoenixLife` (extends JavaPlugin)
- Target Java version: 21
- Uses Paper API 1.21.4-R0.1-SNAPSHOT
- Plugin entry point defined in `plugin.yml`

**Lives System Logic:**
- 10 lives → 1 max heart, 9 lives → 2 max hearts, etc.
- Name colors: Dark Green (10-7 lives), Light Green (6-5), Yellow (4-3), Red (2-1), Black/Gray (0 spectator)
- 0 lives triggers spectator mode with lightning strike and elimination message
- Scoreboard sorted by name tag color

**Required Features to Implement:**
- Lives tracking and persistence
- Max hearts adjustment on death
- Name color changes based on lives
- Round timer (3-hour default)
- Game state management (start/pause/resume)
- Admin commands for lives management
- Lightning strike on final death
- Spectator mode auto-switching

## Testing

Use MockBukkit for unit testing core functionality without manual server testing. Add MockBukkit dependency when implementing tests.

## Development Notes

- Data must be persistent (survive server restarts)
- Configuration should be editable via files and admin commands
- Game can be paused (deaths don't deduct lives, timer paused)
- Players can glow by color group via admin command