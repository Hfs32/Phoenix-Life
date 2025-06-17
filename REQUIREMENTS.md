
This is a Minecraft Paper plugin for version 1.21.4.

**Core requirements**

- The mod must keep track of how many "Lives" each player has, and dynamically adjust their max hearts after each death
- A player will start with 10 lives. One life will be subtracted when a player dies for any reason
- A player's max hearts are inversely correlated to their remaining lives (See table below)

| **# of deaths** | **Lives left** | **Maximum hearts** |
| --------------- | -------------- | ------------------ |
| 0               | 10             | 1                  |
| 1               | 9              | 2                  |
| 2               | 8              | 3                  |
| 3               | 7              | 4                  |
| 4               | 6              | 5                  |
| 5               | 5              | 6                  |
| 6               | 4              | 7                  |
| 7               | 3              | 8                  |
| 8               | 2              | 9                  |
| 9               | 1              | 10                 |
| 10              | 0              | Spectator          |

- The color of each player's name should change color depending on how many lives they have left (See table below)

| **Name tag color** | Lives Left    |
| ------------------ | ------------- |
| Dark Green         | 10-7          |
| Light Green        | 6-5           |
| Yellow             | 4-3           |
| Red                | 2-1           |
| Black/Gray         | 0 (Spectator) |
- When the number of deaths reaches 10, the player's game mode should automatically be changed to Spectator
- If a player is a spectator and they are given more than 0 lives, their game mode should automatically be set to survival
* When a player loses their last life, their death should trigger a lightning strike at their location, and an elimination message should be sent in chat
- The scoreboard should be sorted by name tag color, in the order shown in the table

## Round Timer
* There should be a round timer with a default time of three hours. When the timer is complete, the game should automatically be paused

## Data Storage
* Data should be saved using persistent storage, and configurations such as timer data and remaining lives should be editable from both files and admin commands

## Commands

There should be admin commands added for:
- Starting the game (give 10 lives to each player, round timer starts)
- Pausing the game (deaths don't deduct lives, round timer paused)
- Resuming the game (deaths deduct lives again, round timer resumed)
- Setting a max time for the round timer (HH:MM:SS)
- Setting the remaining time for the round timer (HH:MM:SS)
- Adding lives for a specific player
- Removing lives for a specific player
- Setting a number of lives for a specific player
- Causing players with a specific color (Dark Green, Light Green, Yellow, Red, etc) to glow / be visible to other players


## Testing
* Testing should be done with **[MockBukkit](https://github.com/MockBukkit/MockBukkit)** to ensure that core functionality works without needing to do manual testing
