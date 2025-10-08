package carpet.mixins;

import carpet.utils.ArmorEquipmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Test class to validate vanilla command integration for auto-equipment functionality.
 * This validates the core functionality required by task 4.
 */
public class VanillaCommandIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Vanilla Command Integration Tests...");
        
        try {
            testArmorItemDetection();
            testArmorSlotMapping();
            testArmorComparison();
            testArmorMaterialExtraction();
            
            System.out.println("All vanilla command integration tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Test armor item detection functionality
     * Requirements: 1.2 - Auto-equip armor items when given to fake players
     */
    private static void testArmorItemDetection() {
        System.out.println("Testing armor item detection...");
        
        // Test armor items
        ItemStack[] armorItems = {
            new ItemStack(Items.LEATHER_HELMET),
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(Items.DIAMOND_LEGGINGS),
            new ItemStack(Items.NETHERITE_BOOTS),
            new ItemStack(Items.TURTLE_HELMET)
        };
        
        for (ItemStack armor : armorItems) {
            assert ArmorEquipmentHelper.isArmorItem(armor) : 
                "Item " + armor.getItem() + " should be detected as armor";
            System.out.println("  ✓ " + armor.getItem() + " correctly detected as armor");
        }
        
        // Test non-armor items
        ItemStack[] nonArmorItems = {
            new ItemStack(Items.DIAMOND_SWORD),
            new ItemStack(Items.APPLE),
            new ItemStack(Items.STONE),
            new ItemStack(Items.SHIELD),
            ItemStack.EMPTY
        };
        
        for (ItemStack nonArmor : nonArmorItems) {
            assert !ArmorEquipmentHelper.isArmorItem(nonArmor) : 
                "Item " + nonArmor.getItem() + " should not be detected as armor";
            System.out.println("  ✓ " + nonArmor.getItem() + " correctly not detected as armor");
        }
    }
    
    /**
     * Test armor slot mapping functionality
     * Requirements: 1.2 - Proper slot assignment for auto-equipment
     */
    private static void testArmorSlotMapping() {
        System.out.println("Testing armor slot mapping...");
        
        // Test helmet mapping
        assert ArmorEquipmentHelper.getArmorSlot(Items.LEATHER_HELMET) == EquipmentSlot.HEAD;
        assert ArmorEquipmentHelper.getArmorSlot(Items.IRON_HELMET) == EquipmentSlot.HEAD;
        assert ArmorEquipmentHelper.getArmorSlot(Items.DIAMOND_HELMET) == EquipmentSlot.HEAD;
        assert ArmorEquipmentHelper.getArmorSlot(Items.NETHERITE_HELMET) == EquipmentSlot.HEAD;
        assert ArmorEquipmentHelper.getArmorSlot(Items.TURTLE_HELMET) == EquipmentSlot.HEAD;
        System.out.println("  ✓ All helmets map to HEAD slot");
        
        // Test chestplate mapping
        assert ArmorEquipmentHelper.getArmorSlot(Items.LEATHER_CHESTPLATE) == EquipmentSlot.CHEST;
        assert ArmorEquipmentHelper.getArmorSlot(Items.IRON_CHESTPLATE) == EquipmentSlot.CHEST;
        assert ArmorEquipmentHelper.getArmorSlot(Items.DIAMOND_CHESTPLATE) == EquipmentSlot.CHEST;
        assert ArmorEquipmentHelper.getArmorSlot(Items.NETHERITE_CHESTPLATE) == EquipmentSlot.CHEST;
        System.out.println("  ✓ All chestplates map to CHEST slot");
        
        // Test leggings mapping
        assert ArmorEquipmentHelper.getArmorSlot(Items.LEATHER_LEGGINGS) == EquipmentSlot.LEGS;
        assert ArmorEquipmentHelper.getArmorSlot(Items.IRON_LEGGINGS) == EquipmentSlot.LEGS;
        assert ArmorEquipmentHelper.getArmorSlot(Items.DIAMOND_LEGGINGS) == EquipmentSlot.LEGS;
        assert ArmorEquipmentHelper.getArmorSlot(Items.NETHERITE_LEGGINGS) == EquipmentSlot.LEGS;
        System.out.println("  ✓ All leggings map to LEGS slot");
        
        // Test boots mapping
        assert ArmorEquipmentHelper.getArmorSlot(Items.LEATHER_BOOTS) == EquipmentSlot.FEET;
        assert ArmorEquipmentHelper.getArmorSlot(Items.IRON_BOOTS) == EquipmentSlot.FEET;
        assert ArmorEquipmentHelper.getArmorSlot(Items.DIAMOND_BOOTS) == EquipmentSlot.FEET;
        assert ArmorEquipmentHelper.getArmorSlot(Items.NETHERITE_BOOTS) == EquipmentSlot.FEET;
        System.out.println("  ✓ All boots map to FEET slot");
    }
    
    /**
     * Test armor comparison logic for prioritizing better armor
     * Requirements: 4.2 - Prioritize better armor when replacing
     */
    private static void testArmorComparison() {
        System.out.println("Testing armor comparison logic...");
        
        // Test tier comparison (higher tier is better)
        ItemStack leather = new ItemStack(Items.LEATHER_HELMET);
        ItemStack iron = new ItemStack(Items.IRON_HELMET);
        ItemStack diamond = new ItemStack(Items.DIAMOND_HELMET);
        ItemStack netherite = new ItemStack(Items.NETHERITE_HELMET);
        
        // Test upgrades
        assert ArmorEquipmentHelper.isBetterArmor(leather, iron) : "Iron should be better than leather";
        assert ArmorEquipmentHelper.isBetterArmor(iron, diamond) : "Diamond should be better than iron";
        assert ArmorEquipmentHelper.isBetterArmor(diamond, netherite) : "Netherite should be better than diamond";
        System.out.println("  ✓ Armor tier upgrades work correctly");
        
        // Test downgrades (should not replace)
        assert !ArmorEquipmentHelper.isBetterArmor(iron, leather) : "Leather should not be better than iron";
        assert !ArmorEquipmentHelper.isBetterArmor(diamond, iron) : "Iron should not be better than diamond";
        assert !ArmorEquipmentHelper.isBetterArmor(netherite, diamond) : "Diamond should not be better than netherite";
        System.out.println("  ✓ Armor tier downgrades correctly rejected");
        
        // Test same tier comparison (should consider durability)
        ItemStack freshIron = new ItemStack(Items.IRON_HELMET);
        ItemStack damagedIron = new ItemStack(Items.IRON_HELMET);
        damagedIron.setDamageValue(50); // Add some damage
        
        assert ArmorEquipmentHelper.isBetterArmor(damagedIron, freshIron) : 
            "Fresh armor should be better than damaged armor of same tier";
        assert !ArmorEquipmentHelper.isBetterArmor(freshIron, damagedIron) : 
            "Damaged armor should not be better than fresh armor of same tier";
        System.out.println("  ✓ Same tier durability comparison works correctly");
        
        // Test empty armor comparison
        assert ArmorEquipmentHelper.isBetterArmor(ItemStack.EMPTY, leather) : 
            "Any armor should be better than no armor";
        assert !ArmorEquipmentHelper.isBetterArmor(leather, ItemStack.EMPTY) : 
            "No armor should not be better than existing armor";
        System.out.println("  ✓ Empty armor comparison works correctly");
    }
    
    /**
     * Test armor material extraction for feedback messages
     * Requirements: 4.2 - Send feedback messages about equipment changes
     */
    private static void testArmorMaterialExtraction() {
        System.out.println("Testing armor material extraction...");
        
        // Test material extraction
        assert "leather".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.LEATHER_HELMET)));
        assert "chainmail".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.CHAINMAIL_CHESTPLATE)));
        assert "iron".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.IRON_LEGGINGS)));
        assert "golden".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.GOLDEN_BOOTS)));
        assert "diamond".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.DIAMOND_HELMET)));
        assert "netherite".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.NETHERITE_CHESTPLATE)));
        System.out.println("  ✓ All armor materials extracted correctly");
        
        // Test special cases
        assert ArmorEquipmentHelper.getArmorMaterial(ItemStack.EMPTY) == null : 
            "Empty stack should return null material";
        assert "unknown".equals(ArmorEquipmentHelper.getArmorMaterial(new ItemStack(Items.TURTLE_HELMET))) : 
            "Unknown armor should return 'unknown' material";
        System.out.println("  ✓ Special cases handled correctly");
    }
}