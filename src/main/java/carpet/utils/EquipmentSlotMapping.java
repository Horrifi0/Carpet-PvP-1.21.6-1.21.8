package carpet.utils;

import net.minecraft.world.entity.EquipmentSlot;
import java.util.HashMap;
import java.util.Map;

public enum EquipmentSlotMapping {
    HEAD("head", "helmet", EquipmentSlot.HEAD),
    CHEST("chest", "chestplate", EquipmentSlot.CHEST),
    LEGS("legs", "leggings", EquipmentSlot.LEGS),
    FEET("feet", "boots", EquipmentSlot.FEET),
    MAINHAND("mainhand", "weapon", EquipmentSlot.MAINHAND),
    OFFHAND("offhand", "shield", EquipmentSlot.OFFHAND);

    private final String[] aliases;
    private final EquipmentSlot slot;
    private static final Map<String, EquipmentSlot> SLOT_MAP = new HashMap<>();

    static {
        for (EquipmentSlotMapping mapping : values()) {
            for (String alias : mapping.aliases) {
                SLOT_MAP.put(alias.toLowerCase(), mapping.slot);
            }
        }
    }

    EquipmentSlotMapping(String... aliases) {
        this.aliases = aliases;
        this.slot = EquipmentSlot.valueOf(this.name());
    }

    EquipmentSlotMapping(String alias1, String alias2, EquipmentSlot slot) {
        this.aliases = new String[]{alias1, alias2};
        this.slot = slot;
    }

    public static EquipmentSlot fromString(String name) {
        if (name == null) return null;
        return SLOT_MAP.get(name.toLowerCase());
    }

    public static boolean isValidSlotName(String name) {
        return name != null && SLOT_MAP.containsKey(name.toLowerCase());
    }

    public String[] getAliases() {
        return aliases.clone();
    }

    public EquipmentSlot getSlot() {
        return slot;
    }
}