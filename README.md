# Carpet PvP
![GitHub all releases](https://img.shields.io/github/downloads/AndrewCTF/Carpet-PvP/total?style=for-the-badge)
![GitHub Repo stars](https://img.shields.io/github/stars/AndrewCTF/Carpet-PvP?style=for-the-badge)
![GitHub forks](https://img.shields.io/github/forks/AndrewCTF/Carpet-PvP?style=for-the-badge)
![Users](https://img.shields.io/badge/users-500%2B-blue?style=for-the-badge)

Carpet PvP is a fork of TheobaldTheBird's Carpet PvP, we aim to provide frequent updates so that Carpet PvP will be supported as soon as possible.

Discord: [Carpet PvP Support](https://discord.gg/PAbydjFxKs)

## Table of Contents
- [Supported Versions](#supported-versions)
- [Installation](#installation)
- [Features](#features)
- [Build Instructions](#build-instructions)
- [Recent Bug Fixes](#recent-bug-fixes)
- [Known Issues](#known-issues)
- [Security Policy](#security-policy)
- [Contributing](#contributing)
- [Contributors](#contributors)
- [Code of Conduct](#code-of-conduct)
- [Star History](#star-history)

## Supported Versions

1.21.6-1.21.8

## Installation

1. Download the latest release from the [Releases](https://github.com/AndrewCTF/Carpet-PvP/releases) page
2. Place the `.jar` file in your `mods` folder
3. Ensure you have [Fabric Loader](https://fabricmc.net/use/installer/) and [Fabric API](https://modrinth.com/mod/fabric-api) installed
4. Supported: Minecraft [1.21.6, 1.21.7, 1.21.8] (fabric.mod.json range: 1.21–1.22)

## Features

- **All original Carpet PvP functionality** - Breaks the Map
- **Bot armor auto-equipping system** - Automatically equips best available armor
- **Extended player commands** - Enhanced fake player control
- **Scarpet scripting integration** - Full scripting support
- **Comprehensive testing framework** - Reliable testing tools
- **1.8 combat features** - Spam clicking and block hitting
- **Critical hits working in 1.21.4+** - Fixed gamebreaking crit bug
- **Configurable equipment persistence** - New carpet rule for equipment handling

## Build Instructions

### Quick Build

#### Windows Command Prompt (Recommended)
```cmd
cd C:\Users\Lucas\Desktop\Carpet-PvP
gradlew.bat clean build
```

#### Windows PowerShell
```powershell
cd C:\Users\Lucas\Desktop\Carpet-PvP
.\gradlew.bat clean build
```

#### WSL/Linux (Requires Line Ending Fix)
```bash
cd /mnt/c/Users/Lucas/Desktop/Carpet-PvP
dos2unix gradlew
chmod +x gradlew
./gradlew clean build
```

### Build Requirements
- Java 21
- Gradle wrapper included
- Fabric Loader: 0.16.14
- Fabric API: 0.129.0+1.21.7
- Fabric Loom: 1.10.5

### Development Commands
- **Build**: `./gradlew build -x test`
- **Run client**: `./gradlew runClient`
- **Run server**: `./gradlew runServer`
- **Publish to local maven**: `./gradlew publishToMavenLocal`
- **Clean build**: `./gradlew clean build`

### Output Location
After a successful build, the mod JAR will be located at:
```
build/libs/carpet-pvp-<version>.jar
```

## Recent Bug Fixes

### Critical Fixes (v12.1+v251006)

#### ✅ Critical Hits Now Work in 1.21.4+
- Fake players can now perform critical hits
- No more gamebreaking crit bug
- Map can now be updated to 1.21.4+

#### ✅ Server Crash Fixes
- Fixed shadow player creation causing crashes on death
- Fixed respawned fake players crashing on death
- Added proper spawnPos initialization

#### ✅ Equipment System Fixes
- Equipment handling now properly called on death
- Respects game rules
- New configurable carpet rule: `/carpet fakePlayersKeepEquipment true/false`
- Default: true (keep equipment)

#### ✅ Race Condition Fixes
- All delayed operations have safety checks
- Won't crash if player removed
- Better timing (10ms instead of 1ms for velocity)

#### ✅ Thread Safety Improvements
- spawning Set now uses ConcurrentHashMap
- globalEquipmentStorage now thread-safe
- Proper executor service shutdown

#### ✅ Redstone Wire Randomization Fixed
- Random direction selection now properly returns 0 or 1 (was always returning 0)
- Redstone wire update order now truly randomized

#### ✅ Shield Blocking Fixed
- Axe detection now uses proper ItemTags instead of string matching
- Works with modded axes
- Shield disable function receives player name correctly

### Additional Improvements

- **Better Logging**: Added comprehensive debug logging for troubleshooting
- **Dimension Travel**: Safer with null checks and proper validation
- **End Portal Tracking**: Now logs when fake player beats the game
- **Code Quality**: Removed duplicate code, better organization
- **Error Handling**: Better error messages and exception handling

### Files Modified
- `EntityPlayerMPFake.java` - Main fixes (multiple bugs)
- `CarpetSettings.java` - New rule added
- `Player_fakePlayerCritsMixin.java` - NEW! (fixes crits)
- `carpet.mixins.json` - Registered new mixins
- `RedstoneWireTurbo.java` - Fixed random direction selection
- `MobAICommand.java` - Added error handling
- `AppStoreManager.java` - Fixed array bounds checking
- `SpawnReporter.java` - Fixed substring crashes
- `EntityPlayerActionPack.java` - Fixed drop command

### How to Use New Features

#### Equipment Persistence Rule
```
/carpet fakePlayersKeepEquipment true   # Fake players keep gear (default)
/carpet fakePlayersKeepEquipment false  # Fake players lose gear
```

#### Test Critical Hits
```
/player TestBot spawn
/player TestBot jump
/player TestBot attack
```
You should see critical hit particles! ✨

## Known Issues

### Equipment Not Persisted Across Server Restarts
**Status**: Known Limitation  
**Reason**: Equipment is only stored in-memory, not saved to NBT  
**Impact**: Fake players lose all equipment when server restarts  
**Workaround**: Equipment is preserved during the session  
**Future Enhancement**: Could implement NBT serialization if needed

### Compatibility Notes
- **Paper/Spigot**: Compatibility handled with proper logging
- **Immersive Portals**: Special handling included

## Testing Recommendations

### Critical Tests
1. Spawn fake players: `/player TestBot spawn`
2. Test critical hits (jump + attack)
3. Test death and respawn
4. Test dimension changes (Nether, End)
5. Test equipment persistence
6. Test "drop all" command
7. Test shield blocking with axes
8. Test concurrent fake player spawning

### Performance Tests
- Long-running fake players
- Multiple fake players simultaneously
- Fake player operations during lag

## Practicebot Datapack

The included Practicebot datapack has been updated for Minecraft 1.21.8 compatibility:

### Changes Made
- **Pack Format**: Updated from 48 to 57 (required for 1.21.8)
- **Enchantment Syntax**: Updated 99+ files from nested `levels:{}` format to direct format
- **Schedule Commands**: Fixed `schedule clear` syntax (removed invalid selectors)

### Compatibility
- ✅ Minecraft 1.21.5+ (enchantment syntax change)
- ✅ Minecraft 1.21.6, 1.21.7, 1.21.8
- Requires **Carpet Mod** for custom selectors and player commands

## Security Policy

### Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 12.1.x  | ✅                 |
| 12.1    | :x:                |
| 11.3.x  | :x:                |
| 11.2.x  | :x:                |
| 10.x    | :x:                |

### Reporting a Vulnerability

1. Go to the [Issues](https://github.com/AndrewCTF/Carpet-PvP/issues) page
2. Select the **Security** label
3. Write your issue with detailed description
4. Click "Create" to publish the issue

All security reports will be reviewed within 1-7 days.

## Contributing

Contributors are a very important part of our community - they keep the project updated with the latest patches and features. Contributing is easy:

1. Fork the repository
2. Create a detailed pull request
3. Pull request will be carefully reviewed by admins
4. Review process may take 1 hour to 7 days

### How to Contribute
- Report bugs and issues
- Suggest new features
- Submit pull requests
- Improve documentation
- Test new releases

## Contributors
- [@AndrewCTF](https://github.com/AndrewCTF) (Owner)
- [@Horrifi0](https://github.com/Horrifi0)

## Code of Conduct

### Our Pledge

We pledge to make participation in our community a harassment-free experience for everyone, regardless of age, body size, visible or invisible disability, ethnicity, sex characteristics, gender identity and expression, level of experience, education, socio-economic status, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

**Examples of positive behavior:**
- Demonstrating empathy and kindness toward other people
- Being respectful of differing opinions, viewpoints, and experiences
- Giving and gracefully accepting constructive feedback
- Accepting responsibility and apologizing for mistakes
- Focusing on what is best for the overall community

**Unacceptable behavior:**
- Use of sexualized language or imagery, sexual advances
- Trolling, insulting or derogatory comments, personal or political attacks
- Public or private harassment
- Publishing others' private information without permission
- Other conduct which could be considered inappropriate in a professional setting

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be reported to the community leaders at **email.xryong@gmail.com**.

All complaints will be reviewed and investigated promptly and fairly.

### Enforcement Guidelines

1. **Correction**: Warning for inappropriate language or unprofessional behavior
2. **Warning**: Consequences for continued violations
3. **Temporary Ban**: Serious violations result in temporary community ban
4. **Permanent Ban**: Pattern of violations results in permanent ban

This Code of Conduct is adapted from the [Contributor Covenant](https://www.contributor-covenant.org/), version 2.0.

## Deployment

### Quick Deploy

1. Stop your server
2. Backup current mod JAR and world data
3. Replace old carpet-pvp jar with new one
4. Start server
5. Test: `/player TestBot spawn` and make it jump+attack
6. Verify crits working and no crashes

### Configuration

Set the equipment persistence rule:
```
/carpet fakePlayersKeepEquipment true
```

## Statistics

✅ Critical hits work in 1.21.4+  
✅ No more silent errors  
✅ Equipment system is reliable  
✅ Dimension travel is safer  
✅ Better logging for debugging  
✅ Configurable equipment persistence  
✅ Cleaner, more maintainable code  
✅ Safety checks everywhere  
✅ Thread-safe collections  
✅ Proper resource cleanup  

**Total Bugs Fixed**: 20+ (16 code bugs + 4 mixin registrations)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=andrewctf/carpet-pvp&type=Date)](https://star-history.com/#andrewctf/carpet-pvp&Date)
