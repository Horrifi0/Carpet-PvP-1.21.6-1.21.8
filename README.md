# Carpet PvP

Enhanced fake player controls and PvP features for Minecraft 1.21.6-1.21.8

## New Commands

**Critical Hit Combo:**
```
/player <name> crit
```
Executes jump → attack for automatic critical hits. Targets nearest player.

**Player Tracking:**
```
/player <name> look nearest
```
Continuously tracks nearest player's eyes. Stop with `/player <name> stop`

## Build

```cmd
gradlew.bat clean build
```

Output: `build/libs/carpet-pvp-<version>.jar`
