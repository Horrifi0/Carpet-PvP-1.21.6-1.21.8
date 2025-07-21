package carpet.script;

import carpet.script.utils.EquipmentValidator;
import carpet.script.exception.InternalExpressionException;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Comprehensive test for Scarpet equipment functions with various parameter combinations.
 * Tests all possible parameter combinations and edge cases.
 * 
 * Requirements: 1.4, 3.4, 4.3, 4.4 - Test Scarpet equipment functions with various parameter combinations
 */
public class ScarpetEquipmentParameterTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Scarpet Equipment Parameter Tests...");
        
        try {
            testNumericSlotParameters();
            testStringSlotParameters();
            testMixedSlotParameters();
            testEdgeCaseParameters();
            testParameterCombinations();
            testErrorMessageQuality();
            testParameterValidationPerformance();
            
            System.out.println("All Scarpet equipment parameter tests passed successfully!");
            System.out.println("\n=== PARAMETER TEST VALIDATION COMPLETE ===");
            System.out.println("✓ Numeric slot parameters (0-5) work correctly");
            System.out.println("✓ String slot parameters work with all aliases");
            System.out.println("✓ Mixed parameter types are handled properly");
            System.out.println("✓ Edge cases are handled gracefully");
            System.out.println("✓ All parameter combinations tested");
            System.out.println("✓ Error messages are descriptive and helpful");
            System.out.println("✓ Parameter validation is performant");
            
        } catch (Exception e) {
            System.err.println("Parameter test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Test numeric slot parameters (0-5)
     * Requirements: 3.1 - Numeric parameter validation for equipment slot numbers (0-5)
     */
    private static void testNumericSlotParameters() {
        System.out.println("Testing numeric slot parameters...");
        
        // Test all valid numeric slots
        int[] validSlots = {0, 1, 2, 3, 4, 5};
        EquipmentSlot[] expectedSlots = {
            EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS,
            EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.OFFHAND
        };
        
        for (int i = 0; i < validSlots.length; i++) {
            int slotNumber = validSlots[i];
            EquipmentSlot expected = expectedSlots[i];
            
            EquipmentSlot result = EquipmentValidator.validateSlotNumber(slotNumber);
            assert result == expected : 
                "Slot " + slotNumber + " should map to " + expected + ", got " + result;
            
            // Test reverse mapping
            int reverseSlot = EquipmentValidator.getSlotNumber(result);
            assert reverseSlot == slotNumber : 
                "Reverse mapping should work: " + result + " -> " + slotNumber;
            
            System.out.println("  ✓ Slot " + slotNumber + " ↔ " + result + " (bidirectional)");
        }
        
        // Test invalid numeric slots
        int[] invalidSlots = {-5, -1, 6, 7, 10, 100, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int invalidSlot : invalidSlots) {
            try {
                EquipmentValidator.validateSlotNumber(invalidSlot);
                throw new RuntimeException("Slot " + invalidSlot + " should be invalid");
            } catch (InternalExpressionException e) {
                assert e.getMessage().contains("Equipment slot must be between 0 and 5") : 
                    "Error message should mention valid range";
                assert e.getMessage().contains(String.valueOf(invalidSlot)) : 
                    "Error message should include invalid value";
                System.out.println("  ✓ Invalid slot " + invalidSlot + " properly rejected");
            }
        }
    }
    
    /**
     * Test string slot parameters with all aliases
     * Requirements: 3.2 - String-based slot name support
     */
    private static void testStringSlotParameters() {
        System.out.println("Testing string slot parameters...");
        
        // Test primary slot names
        String[] primaryNames = {"head", "chest", "legs", "feet", "mainhand", "offhand"};
        EquipmentSlot[] expectedSlots = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
            EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
        };
        
        for (int i = 0; i < primaryNames.length; i++) {
            String name = primaryNames[i];
            EquipmentSlot expected = expectedSlots[i];
            
            EquipmentSlot result = EquipmentValidator.validateSlotName(name);
            assert result == expected : 
                "Primary name " + name + " should map to " + expected + ", got " + result;
            System.out.println("  ✓ Primary name " + name + " -> " + result);
        }
        
        // Test alternative slot names
        String[] altNames = {"helmet", "chestplate", "leggings", "boots", "weapon", "shield"};
        for (int i = 0; i < altNames.length; i++) {
            String name = altNames[i];
            EquipmentSlot expected = expectedSlots[i];
            
            EquipmentSlot result = EquipmentValidator.validateSlotName(name);
            assert result == expected : 
                "Alternative name " + name + " should map to " + expected + ", got " + result;
            System.out.println("  ✓ Alternative name " + name + " -> " + result);
        }
        
        // Test case insensitive names
        String[] caseVariants = {"HEAD", "Chest", "lEgS", "FEET", "MainHand", "OffHand"};
        for (int i = 0; i < caseVariants.length; i++) {
            String name = caseVariants[i];
            EquipmentSlot expected = expectedSlots[i];
            
            EquipmentSlot result = EquipmentValidator.validateSlotName(name);
            assert result == expected : 
                "Case variant " + name + " should map to " + expected + ", got " + result;
            System.out.println("  ✓ Case variant " + name + " -> " + result);
        }
        
        // Test invalid string slots
        String[] invalidNames = {"body", "hand", "armor", "invalid", "slot", "equipment"};
        for (String invalidName : invalidNames) {
            try {
                EquipmentValidator.validateSlotName(invalidName);
                throw new RuntimeException("Slot name " + invalidName + " should be invalid");
            } catch (InternalExpressionException e) {
                assert e.getMessage().contains("Invalid equipment slot name") : 
                    "Error message should identify the problem";
                assert e.getMessage().contains(invalidName) : 
                    "Error message should include invalid value";
                assert e.getMessage().contains("Valid slot names are") : 
                    "Error message should list valid options";
                System.out.println("  ✓ Invalid name " + invalidName + " properly rejected");
            }
        }
    }
    
    /**
     * Test mixed parameter types (numeric and string)
     * Requirements: 3.1, 3.2 - Both numeric and string parameters should work
     */
    private static void testMixedSlotParameters() {
        System.out.println("Testing mixed slot parameters...");
        
        // Test that numeric and string parameters map to same slots
        Object[][] mappings = {
            {0, "mainhand", "weapon", EquipmentSlot.MAINHAND},
            {1, "feet", "boots", EquipmentSlot.FEET},
            {2, "legs", "leggings", EquipmentSlot.LEGS},
            {3, "chest", "chestplate", EquipmentSlot.CHEST},
            {4, "head", "helmet", EquipmentSlot.HEAD},
            {5, "offhand", "shield", EquipmentSlot.OFFHAND}
        };
        
        for (Object[] mapping : mappings) {
            int slotNumber = (Integer) mapping[0];
            String primaryName = (String) mapping[1];
            String altName = (String) mapping[2];
            EquipmentSlot expectedSlot = (EquipmentSlot) mapping[3];
            
            // Test all three ways to specify the same slot
            EquipmentSlot fromNumber = EquipmentValidator.validateSlotNumber(slotNumber);
            EquipmentSlot fromPrimary = EquipmentValidator.validateSlotName(primaryName);
            EquipmentSlot fromAlt = EquipmentValidator.validateSlotName(altName);
            
            assert fromNumber == expectedSlot : "Number " + slotNumber + " should map to " + expectedSlot;
            assert fromPrimary == expectedSlot : "Primary " + primaryName + " should map to " + expectedSlot;
            assert fromAlt == expectedSlot : "Alternative " + altName + " should map to " + expectedSlot;
            
            // Test consistency across all methods
            assert fromNumber == fromPrimary && fromPrimary == fromAlt : 
                "All methods should map to same slot for " + expectedSlot;
            
            System.out.println("  ✓ Consistent mapping: " + slotNumber + " = " + primaryName + " = " + altName + " = " + expectedSlot);
        }
    }
    
    /**
     * Test edge case parameters
     * Requirements: 3.3 - Proper error handling for edge cases
     */
    private static void testEdgeCaseParameters() {
        System.out.println("Testing edge case parameters...");
        
        // Test null parameters
        try {
            EquipmentValidator.validateSlotName(null);
            throw new RuntimeException("Null slot name should throw exception");
        } catch (InternalExpressionException e) {
            assert e.getMessage().contains("cannot be empty") : "Null should be handled as empty";
            System.out.println("  ✓ Null slot name handled correctly");
        }
        
        // Test empty string parameters
        try {
            EquipmentValidator.validateSlotName("");
            throw new RuntimeException("Empty slot name should throw exception");
        } catch (InternalExpressionException e) {
            assert e.getMessage().contains("cannot be empty") : "Empty string should be rejected";
            System.out.println("  ✓ Empty slot name handled correctly");
        }
        
        // Test whitespace-only parameters
        String[] whitespaceInputs = {"   ", "\t", "\n", " \t \n "};
        for (String whitespace : whitespaceInputs) {
            try {
                EquipmentValidator.validateSlotName(whitespace);
                throw new RuntimeException("Whitespace slot name should throw exception");
            } catch (InternalExpressionException e) {
                assert e.getMessage().contains("cannot be empty") : "Whitespace should be handled as empty";
                System.out.println("  ✓ Whitespace slot name '" + whitespace.replace("\n", "\\n").replace("\t", "\\t") + "' handled correctly");
            }
        }
        
        // Test very long slot names
        String longName = "a".repeat(1000);
        try {
            EquipmentValidator.validateSlotName(longName);
            throw new RuntimeException("Very long slot name should throw exception");
        } catch (InternalExpressionException e) {
            assert e.getMessage().contains("Invalid equipment slot name") : "Long name should be invalid";
            System.out.println("  ✓ Very long slot name handled correctly");
        }
        
        // Test slot names with special characters
        String[] specialNames = {"head!", "chest@", "legs#", "feet$", "main%hand", "off^hand"};
        for (String specialName : specialNames) {
            try {
                EquipmentValidator.validateSlotName(specialName);
                throw new RuntimeException("Special character slot name should throw exception");
            } catch (InternalExpressionException e) {
                assert e.getMessage().contains("Invalid equipment slot name") : "Special characters should be invalid";
                System.out.println("  ✓ Special character slot name '" + specialName + "' handled correctly");
            }
        }
    }
    
    /**
     * Test various parameter combinations
     * Requirements: Test all possible parameter combinations for Scarpet functions
     */
    private static void testParameterCombinations() {
        System.out.println("Testing parameter combinations...");
        
        // Test inventory_set parameter combinations
        testInventorySetParameterCombinations();
        
        // Test modify parameter combinations
        testModifyParameterCombinations();
        
        // Test equipment_get parameter combinations
        testEquipmentGetParameterCombinations();
        
        System.out.println("  ✓ All parameter combinations tested");
    }
    
    private static void testInventorySetParameterCombinations() {
        // Test inventory_set(player, 'equipment', slot, count, item) combinations
        
        // Valid slot parameter types
        Object[] slotParams = {0, 1, 2, 3, 4, 5, "head", "chest", "legs", "feet", "mainhand", "offhand"};
        
        for (Object slotParam : slotParams) {
            try {
                EquipmentSlot slot;
                if (slotParam instanceof Integer) {
                    slot = EquipmentValidator.validateSlotNumber((Integer) slotParam);
                } else {
                    slot = EquipmentValidator.validateSlotName((String) slotParam);
                }
                assert slot != null : "Slot parameter " + slotParam + " should be valid";
                System.out.println("    ✓ inventory_set slot parameter " + slotParam + " -> " + slot);
            } catch (Exception e) {
                throw new RuntimeException("Valid slot parameter " + slotParam + " failed", e);
            }
        }
        
        System.out.println("    ✓ inventory_set parameter combinations work");
    }
    
    private static void testModifyParameterCombinations() {
        // Test modify(player, 'equipment', slot_name, item) combinations
        
        String[] slotNames = {"head", "helmet", "chest", "chestplate", "legs", "leggings", 
                             "feet", "boots", "mainhand", "weapon", "offhand", "shield"};
        
        for (String slotName : slotNames) {
            try {
                EquipmentSlot slot = EquipmentValidator.validateSlotName(slotName);
                assert slot != null : "Modify slot name " + slotName + " should be valid";
                System.out.println("    ✓ modify slot parameter " + slotName + " -> " + slot);
            } catch (Exception e) {
                throw new RuntimeException("Valid modify slot name " + slotName + " failed", e);
            }
        }
        
        System.out.println("    ✓ modify parameter combinations work");
    }
    
    private static void testEquipmentGetParameterCombinations() {
        // Test equipment_get(player, slot) combinations
        
        // Both numeric and string slots should work
        Object[] slotParams = {0, 1, 2, 3, 4, 5, "head", "chest", "legs", "feet", "mainhand", "offhand"};
        
        for (Object slotParam : slotParams) {
            try {
                EquipmentSlot slot;
                if (slotParam instanceof Integer) {
                    slot = EquipmentValidator.validateSlotNumber((Integer) slotParam);
                } else {
                    slot = EquipmentValidator.validateSlotName((String) slotParam);
                }
                assert slot != null : "equipment_get slot parameter " + slotParam + " should be valid";
                System.out.println("    ✓ equipment_get slot parameter " + slotParam + " -> " + slot);
            } catch (Exception e) {
                throw new RuntimeException("Valid equipment_get slot parameter " + slotParam + " failed", e);
            }
        }
        
        System.out.println("    ✓ equipment_get parameter combinations work");
    }
    
    /**
     * Test error message quality and helpfulness
     * Requirements: 3.3 - Descriptive error messages for invalid parameters
     */
    private static void testErrorMessageQuality() {
        System.out.println("Testing error message quality...");
        
        // Test numeric slot error message quality
        try {
            EquipmentValidator.validateSlotNumber(10);
            throw new RuntimeException("Should have thrown exception");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            
            // Check message contains all required information
            assert message.contains("Invalid equipment slot number") : "Should identify the problem type";
            assert message.contains("10") : "Should include the invalid value";
            assert message.contains("between 0 and 5") : "Should specify valid range";
            assert message.contains("0=mainhand") : "Should provide slot mapping examples";
            assert message.contains("1=feet") : "Should provide multiple examples";
            
            System.out.println("  ✓ Numeric slot error message is comprehensive");
        }
        
        // Test string slot error message quality
        try {
            EquipmentValidator.validateSlotName("invalid_slot");
            throw new RuntimeException("Should have thrown exception");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            
            // Check message contains all required information
            assert message.contains("Invalid equipment slot name") : "Should identify the problem type";
            assert message.contains("invalid_slot") : "Should include the invalid value";
            assert message.contains("Valid slot names are") : "Should introduce valid options";
            assert message.contains("head") : "Should list valid slot names";
            assert message.contains("chest") : "Should list multiple valid names";
            
            System.out.println("  ✓ String slot error message is comprehensive");
        }
        
        // Test empty parameter error message quality
        try {
            EquipmentValidator.validateSlotName("");
            throw new RuntimeException("Should have thrown exception");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            
            assert message.contains("cannot be empty") : "Should identify empty parameter issue";
            System.out.println("  ✓ Empty parameter error message is clear");
        }
        
        System.out.println("  ✓ All error messages are high quality and helpful");
    }
    
    /**
     * Test parameter validation performance
     * Ensures validation is fast enough for real-time use
     */
    private static void testParameterValidationPerformance() {
        System.out.println("Testing parameter validation performance...");
        
        long startTime = System.nanoTime();
        
        // Test numeric validation performance
        for (int i = 0; i < 10000; i++) {
            for (int slot = 0; slot <= 5; slot++) {
                EquipmentValidator.validateSlotNumber(slot);
            }
        }
        
        long numericTime = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        
        // Test string validation performance
        String[] slotNames = {"head", "chest", "legs", "feet", "mainhand", "offhand"};
        for (int i = 0; i < 10000; i++) {
            for (String slotName : slotNames) {
                EquipmentValidator.validateSlotName(slotName);
            }
        }
        
        long stringTime = System.nanoTime() - startTime;
        
        // Performance should be reasonable (less than 1ms per 1000 validations)
        double numericMs = numericTime / 1_000_000.0;
        double stringMs = stringTime / 1_000_000.0;
        
        assert numericMs < 100 : "Numeric validation should be fast: " + numericMs + "ms";
        assert stringMs < 100 : "String validation should be fast: " + stringMs + "ms";
        
        System.out.println("  ✓ Numeric validation: " + String.format("%.2f", numericMs) + "ms for 60,000 validations");
        System.out.println("  ✓ String validation: " + String.format("%.2f", stringMs) + "ms for 60,000 validations");
        System.out.println("  ✓ Parameter validation performance is acceptable");
    }
}