package carpet.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling armor equipment logic for fake players
 */
public class ArmorEquipmentHelper {
    
    // Map armor items to their equipment slots
    private static final Map<Item, EquipmentSlot> ARMOR_SLOT_MAP = new HashMap<>();
    
    // Armor tier values for comparison (higher is better)
    private static final Map<String, Integer> ARMOR_TIER_VALUES = Map.of(
        "leather", 1,
        "chainmail", 2,
        "golden", 3,
        "iron", 4,
        "diamond", 5,
        "netherite", 6
    );
    
    static {
        // Initialize armor slot mappings
        ARMOR_SLOT_MAP.put(Items.LEATHER_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.LEATHER_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.LEATHER_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.LEATHER_BOOTS, EquipmentSlot.FEET);
        
        ARMOR_SLOT_MAP.put(Items.CHAINMAIL_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.CHAINMAIL_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.CHAINMAIL_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.CHAINMAIL_BOOTS, EquipmentSlot.FEET);
        
        ARMOR_SLOT_MAP.put(Items.IRON_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.IRON_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.IRON_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.IRON_BOOTS, EquipmentSlot.FEET);
        
        ARMOR_SLOT_MAP.put(Items.GOLDEN_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.GOLDEN_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.GOLDEN_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.GOLDEN_BOOTS, EquipmentSlot.FEET);
        
        ARMOR_SLOT_MAP.put(Items.DIAMOND_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.DIAMOND_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.DIAMOND_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.DIAMOND_BOOTS, EquipmentSlot.FEET);
        
        ARMOR_SLOT_MAP.put(Items.NETHERITE_HELMET, EquipmentSlot.HEAD);
        ARMOR_SLOT_MAP.put(Items.NETHERITE_CHESTPLATE, EquipmentSlot.CHEST);
        ARMOR_SLOT_MAP.put(Items.NETHERITE_LEGGINGS, EquipmentSlot.LEGS);
        ARMOR_SLOT_MAP.put(Items.NETHERITE_BOOTS, EquipmentSlot.FEET);
        
        // Add turtle helmet
        ARMOR_SLOT_MAP.put(Items.TURTLE_HELMET, EquipmentSlot.HEAD);
    }
    
    /**
     * Checks if an item is armor
     */
    public static boolean isArmorItem(Item item) {
        return ARMOR_SLOT_MAP.containsKey(item);
    }
    
    /**
     * Checks if an ItemStack is armor
     */
    public static boolean isArmorItem(ItemStack stack) {
        return !stack.isEmpty() && isArmorItem(stack.getItem());
    }
    
    /**
     * Gets the equipment slot for an armor item
     */
    public static EquipmentSlot getArmorSlot(Item item) {
        return ARMOR_SLOT_MAP.get(item);
    }
    
    /**
     * Gets the equipment slot for an armor ItemStack
     */
    public static EquipmentSlot getArmorSlot(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return getArmorSlot(stack.getItem());
    }
    
    /**
     * Compares two armor items to determine which is better
     * Returns true if newArmor is better than currentArmor
     */
    public static boolean isBetterArmor(ItemStack currentArmor, ItemStack newArmor) {
        if (currentArmor.isEmpty()) {
            return true; // Any armor is better than no armor
        }
        
        if (newArmor.isEmpty()) {
            return false; // No armor is not better than existing armor
        }
        
        // Get armor tier values
        int currentTier = getArmorTier(currentArmor);
        int newTier = getArmorTier(newArmor);
        
        if (newTier > currentTier) {
            return true; // Higher tier is better
        }
        
        if (newTier == currentTier) {
            // Same tier, compare durability (higher durability is better)
            int currentDurability = currentArmor.getMaxDamage() - currentArmor.getDamageValue();
            int newDurability = newArmor.getMaxDamage() - newArmor.getDamageValue();
            return newDurability > currentDurability;
        }
        
        return false; // Lower tier is not better
    }
    
    /**
     * Gets the tier value of an armor item
     */
    private static int getArmorTier(ItemStack armor) {
        String itemName = BuiltInRegistries.ITEM.getKey(armor.getItem()).getPath();
        
        for (Map.Entry<String, Integer> entry : ARMOR_TIER_VALUES.entrySet()) {
            if (itemName.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default tier for unknown armor
        return 0;
    }
    
    /**
     * Gets the material name from an armor item
     */
    public static String getArmorMaterial(ItemStack armor) {
        if (armor.isEmpty()) {
            return null;
        }
        
        String itemName = BuiltInRegistries.ITEM.getKey(armor.getItem()).getPath();
        
        for (String material : ARMOR_TIER_VALUES.keySet()) {
            if (itemName.startsWith(material)) {
                return material;
            }
        }
        
        return "unknown";
    }
}