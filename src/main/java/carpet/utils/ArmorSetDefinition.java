package carpet.utils;

import net.minecraft.world.entity.EquipmentSlot;
import java.util.HashMap;
import java.util.Map;

public class ArmorSetDefinition {
    private final String materialName;
    private final Map<EquipmentSlot, String> pieces;

    public ArmorSetDefinition(String materialName, Map<EquipmentSlot, String> pieces) {
        this.materialName = materialName;
        this.pieces = new HashMap<>(pieces);
    }

    public static final Map<String, ArmorSetDefinition> ARMOR_SETS = Map.of(
        "leather", new ArmorSetDefinition("leather", Map.of(
            EquipmentSlot.HEAD, "leather_helmet",
            EquipmentSlot.CHEST, "leather_chestplate",
            EquipmentSlot.LEGS, "leather_leggings",
            EquipmentSlot.FEET, "leather_boots"
        )),
        "chainmail", new ArmorSetDefinition("chainmail", Map.of(
            EquipmentSlot.HEAD, "chainmail_helmet",
            EquipmentSlot.CHEST, "chainmail_chestplate",
            EquipmentSlot.LEGS, "chainmail_leggings",
            EquipmentSlot.FEET, "chainmail_boots"
        )),
        "iron", new ArmorSetDefinition("iron", Map.of(
            EquipmentSlot.HEAD, "iron_helmet",
            EquipmentSlot.CHEST, "iron_chestplate",
            EquipmentSlot.LEGS, "iron_leggings",
            EquipmentSlot.FEET, "iron_boots"
        )),
        "golden", new ArmorSetDefinition("golden", Map.of(
            EquipmentSlot.HEAD, "golden_helmet",
            EquipmentSlot.CHEST, "golden_chestplate",
            EquipmentSlot.LEGS, "golden_leggings",
            EquipmentSlot.FEET, "golden_boots"
        )),
        "gold", new ArmorSetDefinition("gold", Map.of(
            EquipmentSlot.HEAD, "golden_helmet",
            EquipmentSlot.CHEST, "golden_chestplate",
            EquipmentSlot.LEGS, "golden_leggings",
            EquipmentSlot.FEET, "golden_boots"
        )),
        "diamond", new ArmorSetDefinition("diamond", Map.of(
            EquipmentSlot.HEAD, "diamond_helmet",
            EquipmentSlot.CHEST, "diamond_chestplate",
            EquipmentSlot.LEGS, "diamond_leggings",
            EquipmentSlot.FEET, "diamond_boots"
        )),
        "netherite", new ArmorSetDefinition("netherite", Map.of(
            EquipmentSlot.HEAD, "netherite_helmet",
            EquipmentSlot.CHEST, "netherite_chestplate",
            EquipmentSlot.LEGS, "netherite_leggings",
            EquipmentSlot.FEET, "netherite_boots"
        ))
    );

    public String getMaterialName() {
        return materialName;
    }

    public Map<EquipmentSlot, String> getPieces() {
        return new HashMap<>(pieces);
    }

    public String getItemForSlot(EquipmentSlot slot) {
        return pieces.get(slot);
    }

    public static ArmorSetDefinition getArmorSet(String name) {
        if (name == null) return null;
        return ARMOR_SETS.get(name.toLowerCase());
    }

    public static boolean isValidArmorSet(String name) {
        return name != null && ARMOR_SETS.containsKey(name.toLowerCase());
    }
}