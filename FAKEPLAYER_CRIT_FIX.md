# Fake Player Critical Hit Fix for Minecraft 1.21.4+

## Problem
In Minecraft 1.21.4, fake players created by Carpet PvP were unable to perform critical hits. This was a gamebreaking bug that prevented the map from being updated to 1.21.4 and beyond.

## Root Cause
The issue was caused by changes to movement mechanics in Minecraft 1.21.4. Critical hits require the following conditions:
1. Player must be falling (getDeltaMovement().y < 0)
2. Player must not be on ground (`onGround() == false`)
3. Player must not be sprinting
4. Player must not be climbing
5. Player must not be in water

The problem was that fake players had their `onGround` flag incorrectly set to `true` even when they were falling, which prevented them from performing critical hits.

## Solution
A new mixin `Player_fakePlayerCritsMixin` was created that redirects the `onGround()` check in the `Player.attack()` method specifically for fake players. When a fake player is falling (velocity.y < 0), the mixin forces the `onGround()` check to return `false`, allowing critical hits to work properly.

## Implementation Details

### Files Created/Modified:
1. **New File**: `src/main/java/carpet/mixins/Player_fakePlayerCritsMixin.java`
   - Contains the mixin that fixes the critical hit detection for fake players
   - Uses `@Redirect` to intercept the `onGround()` check in the `attack` method
   - Only affects fake players (`EntityPlayerMPFake` instances)
   - Checks if fake player is falling and returns `false` for `onGround()` in that case

2. **Modified File**: `src/main/resources/carpet.mixins.json`
   - Added `Player_fakePlayerCritsMixin` to the list of mixins

## How It Works
The mixin works by:
1. Intercepting the `onGround()` call in the `Player.attack()` method
2. Checking if the player is a fake player (`instanceof EntityPlayerMPFake`)
3. If it's a fake player and they're falling (`getDeltaMovement().y < 0`), returning `false`
4. Otherwise, using the original `onGround()` value

This ensures that fake players can perform critical hits when they should be able to, while maintaining normal behavior for real players.

## Testing
To test the fix:
1. Build the mod: `./gradlew build -x test`
2. Install the built jar from `build/libs/carpet-pvp-1.21.8-12.1+v251006.jar`
3. Create a fake player using `/player <name> spawn`
4. Make the fake player jump and attack (using action pack)
5. Verify that critical hit particles appear when the fake player is falling

## Compatibility
- **Minecraft Versions**: 1.21.6-1.21.8 (as specified in gradle.properties and fabric.mod.json)
- **Fabric Loader**: Latest
- **Fabric API**: Latest

This fix should work for all future versions unless Mojang makes significant changes to the attack/critical hit mechanics.

## Notes
- The fix only affects fake players, real players continue to use vanilla critical hit mechanics
- The mixin is designed to be minimal and non-intrusive
- No changes were made to the fake player entity itself
- The fix is backwards compatible with older versions of Carpet PvP
