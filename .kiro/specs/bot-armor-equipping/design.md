# Bot Armor Equipping - Design Document

## Overview

The bot armor equipping system will be implemented through multiple components working together to provide seamless equipment functionality for fake players. The design addresses current issues with Scarpet syntax errors, vanilla command compatibility, and equipment synchronization.

## Architecture

The system consists of four main components:

1. **Enhanced PlayerCommand** - Extended `/player` command with equipment subcommands
2. **Fixed Scarpet Integration** - Corrected equipment manipulation functions
3. **Equipment Synchronization System** - Ensures proper client-server equipment sync
4. **Vanilla Command Compatibility** - Integration with standard Minecraft `/give` command

## Components and Interfaces

### 1. Enhanced PlayerCommand

**Location:** `src/main/java/carpet/commands/PlayerCommand.java`

**New Subcommands:**
- `/player <name> equip <armor_type>` - Equips full armor set (leather, iron, diamond, netherite, etc.)
- `/player <name> equip <slot> <item>` - Equips specific item in equipment slot
- `/player <name> unequip <slot>` - Removes equipment from specific slot
- `/player <name> equipment` - Shows current equipment status

**Equipment Slot Mapping:**
- `head` / `helmet` → EquipmentSlot.HEAD
- `chest` / `chestplate` → EquipmentSlot.CHEST  
- `legs` / `leggings` → EquipmentSlot.LEGS
- `feet` / `boots` → EquipmentSlot.FEET
- `mainhand` / `weapon` → EquipmentSlot.MAINHAND
- `offhand` / `shield` → EquipmentSlot.OFFHAND

### 2. Fixed Scarpet Integration

**Location:** `src/main/java/carpet/script/api/Inventories.java`

**Enhanced Functions:**
- `inventory_set(player, 'equipment', slot, count, item)` - Fixed parameter parsing
- `modify(player, 'equipment', slot_name, item)` - Simplified equipment modification
- `equipment_get(player, slot)` - Get current equipment in slot
- `equipment_clear(player, slot)` - Clear equipment slot

**Parameter Validation:**
- Proper numeric slot validation (0-5 for equipment slots)
- String-based slot name support ('head', 'chest', etc.)
- Item name validation with registry lookup
- NBT tag support for enchanted equipment

### 3. Equipment Synchronization System

**Location:** `src/main/java/carpet/patches/EntityPlayerMPFake.java`

**Enhanced onEquipItem Method:**
```java
@Override
public void onEquipItem(final EquipmentSlot slot, final ItemStack previous, final ItemStack stack)
{
    super.onEquipItem(slot, previous, stack);
    // Force synchronization to all clients
    syncEquipmentToClients(slot, stack);
}

private void syncEquipmentToClients(EquipmentSlot slot, ItemStack stack) {
    // Send equipment update packets to all nearby players
    List<ServerPlayer> nearbyPlayers = this.serverLevel().getPlayers(
        player -> player.distanceToSqr(this) < 64 * 64
    );
    
    for (ServerPlayer player : nearbyPlayers) {
        player.connection.send(new ClientboundSetEquipmentPacket(
            this.getId(), 
            List.of(Pair.of(slot, stack))
        ));
    }
}
```

### 4. Vanilla Command Compatibility

**Location:** `src/main/java/carpet/mixins/PlayerList_fakePlayersMixin.java`

**Auto-Equipment Logic:**
- Intercept `/give` commands targeting fake players
- Automatically equip armor items to appropriate slots if slots are empty
- Prioritize better armor when replacing existing equipment
- Send feedback to command sender about equipment changes

## Data Models

### EquipmentSlotMapping
```java
public enum EquipmentSlotMapping {
    HEAD("head", "helmet", EquipmentSlot.HEAD),
    CHEST("chest", "chestplate", EquipmentSlot.CHEST),
    LEGS("legs", "leggings", EquipmentSlot.LEGS),
    FEET("feet", "boots", EquipmentSlot.FEET),
    MAINHAND("mainhand", "weapon", EquipmentSlot.MAINHAND),
    OFFHAND("offhand", "shield", EquipmentSlot.OFFHAND);
    
    private final String[] aliases;
    private final EquipmentSlot slot;
    
    public static EquipmentSlot fromString(String name) {
        // Implementation for string-to-slot conversion
    }
}
```

### ArmorSetDefinition
```java
public class ArmorSetDefinition {
    private final String materialName;
    private final Map<EquipmentSlot, String> pieces;
    
    public static final Map<String, ArmorSetDefinition> ARMOR_SETS = Map.of(
        "leather", new ArmorSetDefinition("leather", Map.of(
            EquipmentSlot.HEAD, "leather_helmet",
            EquipmentSlot.CHEST, "leather_chestplate",
            EquipmentSlot.LEGS, "leather_leggings",
            EquipmentSlot.FEET, "leather_boots"
        )),
        "diamond", new ArmorSetDefinition("diamond", Map.of(
            EquipmentSlot.HEAD, "diamond_helmet",
            EquipmentSlot.CHEST, "diamond_chestplate",
            EquipmentSlot.LEGS, "diamond_leggings",
            EquipmentSlot.FEET, "diamond_boots"
        ))
        // ... other armor sets
    );
}
```

## Error Handling

### Command Validation
- Validate fake player exists before equipment operations
- Check item registry for valid item names
- Validate equipment slot names and numbers
- Provide helpful error messages with correct syntax examples

### Scarpet Function Error Handling
- Proper parameter type checking with clear error messages
- Graceful handling of invalid slot numbers/names
- Registry validation for item names
- NBT parsing error handling

### Synchronization Error Recovery
- Retry mechanism for failed equipment sync packets
- Fallback to full player data sync if equipment sync fails
- Logging for debugging synchronization issues

## Testing Strategy

### Unit Tests
- Test equipment slot mapping functions
- Test armor set definition lookups
- Test parameter validation logic
- Test NBT parsing and item creation

### Integration Tests
- Test `/player equip` commands with various parameters
- Test Scarpet equipment functions with different syntaxes
- Test vanilla `/give` command auto-equipment
- Test equipment persistence across dimension changes

### Manual Testing Scenarios
1. Spawn fake player and equip full diamond armor set
2. Use Scarpet to equip individual armor pieces
3. Use `/give` to provide armor and verify auto-equipping
4. Test equipment visibility from other players' perspectives
5. Test armor protection during combat scenarios
6. Test equipment persistence across server restart

## Performance Considerations

### Equipment Synchronization
- Batch equipment updates when possible
- Limit sync frequency to prevent packet spam
- Only sync to players within render distance
- Cache equipment state to avoid redundant updates

### Command Processing
- Pre-validate parameters before expensive operations
- Cache armor set definitions for quick lookup
- Optimize item registry lookups
- Use efficient data structures for slot mapping

## Security Considerations

### Permission Checks
- Verify player has permission to modify fake player equipment
- Validate that target is actually a fake player for restricted operations
- Check item spawn permissions for valuable items
- Audit equipment changes for administrative oversight

### Input Validation
- Sanitize all user input for item names and NBT data
- Prevent injection attacks through NBT manipulation
- Validate slot numbers are within acceptable ranges
- Check for malformed Scarpet expressions