package carpet.utils;

import net.minecraft.world.entity.EquipmentSlot;

/**
 * Unit test for ArmorSetDefinition utility class.
 * Tests equipment slot mapping and validation functionality.
 * 
 * Requirements: 1.4, 3.4, 4.3, 4.4 - Unit tests for equipment slot mapping and validation
 */
public class ArmorSetDefinitionTest {
    
    public static void main(String[] args) {
        System.out.println("Starting ArmorSetDefinition Unit Tests...");
        
        try {
            testArmorSetRetrieval();
            testArmorSetValidation();
            testArmorSetPieces();
            testArmorSetMaterialNames();
            testCaseInsensitiveRetrieval();
            testNullAndEmptyHandling();
            testAllDefinedArmorSets();
            
            System.out.println("All ArmorSetDefinition unit tests passed successfully!");
            System.out.println("\n=== UNIT TEST VALIDATION COMPLETE ===");
            System.out.println("✓ Equipment slot mapping works correctly");
            System.out.println("✓ Armor set validation functions properly");
            System.out.println("✓ All armor types are properly defined");
            System.out.println("✓ Case-insensitive retrieval works");
            System.out.println("✓ Error handling for invalid inputs works");
            
        } catch (Exception e) {
            System.err.println("Unit test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Test armor set retrieval functionality
     */
    private static void testArmorSetRetrieval() {
        System.out.println("Testing armor set retrieval...");
        
        // Test valid armor sets
        String[] validSets = {"leather", "chainmail", "iron", "golden", "gold", "diamond", "netherite"};
        for (String setName : validSets) {
            ArmorSetDefinition set = ArmorSetDefinition.getArmorSet(setName);
            assert set != null : "Armor set '" + setName + "' should exist";
            assert set.getMaterialName().equals(setName) || 
                   (setName.equals("gold") && set.getMaterialName().equals("gold")) : 
                   "Material name should match for " + setName;
            System.out.println("  ✓ " + setName + " armor set retrieved successfully");
        }
        
        // Test invalid armor sets
        String[] invalidSets = {"wood", "stone", "invalid", "copper", "emerald"};
        for (String setName : invalidSets) {
            ArmorSetDefinition set = ArmorSetDefinition.getArmorSet(setName);
            assert set == null : "Invalid armor set '" + setName + "' should return null";
            System.out.println("  ✓ Invalid set '" + setName + "' correctly returns null");
        }
    }
    
    /**
     * Test armor set validation functionality
     */
    private static void testArmorSetValidation() {
        System.out.println("Testing armor set validation...");
        
        // Test valid armor sets
        assert ArmorSetDefinition.isValidArmorSet("leather");
        assert ArmorSetDefinition.isValidArmorSet("iron");
        assert ArmorSetDefinition.isValidArmorSet("diamond");
        assert ArmorSetDefinition.isValidArmorSet("netherite");
        assert ArmorSetDefinition.isValidArmorSet("golden");
        assert ArmorSetDefinition.isValidArmorSet("gold"); // alias
        assert ArmorSetDefinition.isValidArmorSet("chainmail");
        System.out.println("  ✓ All valid armor sets pass validation");
        
        // Test invalid armor sets
        assert !ArmorSetDefinition.isValidArmorSet("wood");
        assert !ArmorSetDefinition.isValidArmorSet("stone");
        assert !ArmorSetDefinition.isValidArmorSet("invalid");
        assert !ArmorSetDefinition.isValidArmorSet("");
        assert !ArmorSetDefinition.isValidArmorSet(null);
        System.out.println("  ✓ All invalid armor sets fail validation");
        
        // Test case insensitivity
        assert ArmorSetDefinition.isValidArmorSet("LEATHER");
        assert ArmorSetDefinition.isValidArmorSet("Iron");
        assert ArmorSetDefinition.isValidArmorSet("DiAmOnD");
        System.out.println("  ✓ Case insensitive validation works");
    }
    
    /**
     * Test armor set pieces mapping
     */
    private static void testArmorSetPieces() {
        System.out.println("Testing armor set pieces mapping...");
        
        ArmorSetDefinition diamond = ArmorSetDefinition.getArmorSet("diamond");
        assert diamond != null : "Diamond armor set should exist";
        
        // Test all equipment slots are mapped
        assert diamond.getItemForSlot(EquipmentSlot.HEAD).equals("diamond_helmet");
        assert diamond.getItemForSlot(EquipmentSlot.CHEST).equals("diamond_chestplate");
        assert diamond.getItemForSlot(EquipmentSlot.LEGS).equals("diamond_leggings");
        assert diamond.getItemForSlot(EquipmentSlot.FEET).equals("diamond_boots");
        System.out.println("  ✓ Diamond armor pieces map correctly");
        
        // Test that non-armor slots return null
        assert diamond.getItemForSlot(EquipmentSlot.MAINHAND) == null;
        assert diamond.getItemForSlot(EquipmentSlot.OFFHAND) == null;
        System.out.println("  ✓ Non-armor slots correctly return null");
        
        // Test pieces map is immutable
        var pieces = diamond.getPieces();
        int originalSize = pieces.size();
        pieces.put(EquipmentSlot.MAINHAND, "diamond_sword"); // Should not affect original
        assert diamond.getPieces().size() == originalSize : "Original pieces map should be immutable";
        assert diamond.getItemForSlot(EquipmentSlot.MAINHAND) == null : "Original should not be modified";
        System.out.println("  ✓ Pieces map is properly immutable");
    }
    
    /**
     * Test armor set material names
     */
    private static void testArmorSetMaterialNames() {
        System.out.println("Testing armor set material names...");
        
        // Test material name consistency
        String[] materials = {"leather", "chainmail", "iron", "golden", "diamond", "netherite"};
        for (String material : materials) {
            ArmorSetDefinition set = ArmorSetDefinition.getArmorSet(material);
            assert set.getMaterialName().equals(material) : 
                "Material name should match for " + material;
            System.out.println("  ✓ " + material + " material name is consistent");
        }
        
        // Test gold alias
        ArmorSetDefinition gold = ArmorSetDefinition.getArmorSet("gold");
        assert gold.getMaterialName().equals("gold") : "Gold alias should have correct material name";
        System.out.println("  ✓ Gold alias material name is correct");
    }
    
    /**
     * Test case insensitive retrieval
     */
    private static void testCaseInsensitiveRetrieval() {
        System.out.println("Testing case insensitive retrieval...");
        
        // Test various case combinations
        String[][] testCases = {
            {"leather", "LEATHER", "Leather", "lEaThEr"},
            {"iron", "IRON", "Iron", "iRoN"},
            {"diamond", "DIAMOND", "Diamond", "DiAmOnD"},
            {"netherite", "NETHERITE", "Netherite", "NeTHerITe"}
        };
        
        for (String[] cases : testCases) {
            ArmorSetDefinition reference = ArmorSetDefinition.getArmorSet(cases[0]);
            for (int i = 1; i < cases.length; i++) {
                ArmorSetDefinition test = ArmorSetDefinition.getArmorSet(cases[i]);
                assert test != null : "Case variant '" + cases[i] + "' should work";
                assert test.getMaterialName().equals(reference.getMaterialName()) : 
                    "Case variant should return same armor set";
            }
            System.out.println("  ✓ " + cases[0] + " case variants work correctly");
        }
    }
    
    /**
     * Test null and empty string handling
     */
    private static void testNullAndEmptyHandling() {
        System.out.println("Testing null and empty string handling...");
        
        // Test null handling
        assert ArmorSetDefinition.getArmorSet(null) == null : "Null should return null";
        assert !ArmorSetDefinition.isValidArmorSet(null) : "Null should not be valid";
        System.out.println("  ✓ Null input handled correctly");
        
        // Test empty string handling
        assert ArmorSetDefinition.getArmorSet("") == null : "Empty string should return null";
        assert !ArmorSetDefinition.isValidArmorSet("") : "Empty string should not be valid";
        System.out.println("  ✓ Empty string handled correctly");
        
        // Test whitespace handling
        assert ArmorSetDefinition.getArmorSet("   ") == null : "Whitespace should return null";
        assert !ArmorSetDefinition.isValidArmorSet("   ") : "Whitespace should not be valid";
        System.out.println("  ✓ Whitespace input handled correctly");
    }
    
    /**
     * Test all defined armor sets have complete definitions
     */
    private static void testAllDefinedArmorSets() {
        System.out.println("Testing all defined armor sets are complete...");
        
        EquipmentSlot[] requiredSlots = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
        
        for (String setName : ArmorSetDefinition.ARMOR_SETS.keySet()) {
            ArmorSetDefinition set = ArmorSetDefinition.getArmorSet(setName);
            assert set != null : "Armor set " + setName + " should be retrievable";
            
            // Check all required slots are defined
            for (EquipmentSlot slot : requiredSlots) {
                String item = set.getItemForSlot(slot);
                assert item != null : "Armor set " + setName + " missing " + slot.getName() + " piece";
                assert !item.trim().isEmpty() : "Armor set " + setName + " has empty " + slot.getName() + " piece";
                assert item.contains(setName) || (setName.equals("gold") && item.contains("golden")) : 
                    "Item " + item + " should contain material name " + setName;
            }
            
            System.out.println("  ✓ " + setName + " armor set is complete");
        }
        
        // Verify expected number of armor sets
        int expectedSets = 7; // leather, chainmail, iron, golden, gold, diamond, netherite
        assert ArmorSetDefinition.ARMOR_SETS.size() == expectedSets : 
            "Expected " + expectedSets + " armor sets, found " + ArmorSetDefinition.ARMOR_SETS.size();
        System.out.println("  ✓ All " + expectedSets + " expected armor sets are defined");
    }
}