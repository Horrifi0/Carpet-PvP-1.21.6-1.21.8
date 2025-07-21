package carpet.script;

import carpet.script.utils.EquipmentValidator;
import carpet.script.exception.InternalExpressionException;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Simple test class to validate equipment functions without external test frameworks.
 * This validates the core functionality required by task 3.
 */
public class EquipmentValidationTest {
    
    public static void main(String[] args) {
        System.out.println("Starting Equipment Validation Tests...");
        
        try {
            testSlotNumberValidation();
            testSlotNameValidation();
            testErrorMessages();
            testSlotMapping();
            
            System.out.println("All tests passed successfully!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Test numeric parameter validation for equipment slot numbers (0-5)
     * Requirements: 3.1, 3.3 - Proper parameter validation
     */
    private static void testSlotNumberValidation() {
        System.out.println("Testing slot number validation...");
        
        // Test valid slot numbers (0-5)
        for (int slot = 0; slot <= 5; slot++) {
            try {
                EquipmentSlot result = EquipmentValidator.validateSlotNumber(slot);
                assert result != null : "Slot " + slot + " should return a valid EquipmentSlot";
                System.out.println("  ✓ Slot " + slot + " -> " + result);
            } catch (Exception e) {
                throw new RuntimeException("Slot " + slot + " should be valid", e);
            }
        }
        
        // Test invalid slot numbers
        int[] invalidSlots = {-1, 6, 10, 100};
        for (int invalidSlot : invalidSlots) {
            try {
                EquipmentValidator.validateSlotNumber(invalidSlot);
                throw new RuntimeException("Slot " + invalidSlot + " should be invalid");
            } catch (InternalExpressionException e) {
                System.out.println("  ✓ Invalid slot " + invalidSlot + " correctly rejected: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test string-based slot name support
     * Requirements: 3.2 - String-based slot name support
     */
    private static void testSlotNameValidation() {
        System.out.println("Testing slot name validation...");
        
        // Test valid string slot names
        String[] validSlotNames = {"head", "chest", "legs", "feet", "mainhand", "offhand"};
        String[] alternativeNames = {"helmet", "chestplate", "leggings", "boots", "weapon", "shield"};
        
        for (String slotName : validSlotNames) {
            try {
                EquipmentSlot result = EquipmentValidator.validateSlotName(slotName);
                assert result != null : "Slot name '" + slotName + "' should return a valid EquipmentSlot";
                System.out.println("  ✓ Slot name '" + slotName + "' -> " + result);
            } catch (Exception e) {
                throw new RuntimeException("Slot name '" + slotName + "' should be valid", e);
            }
        }
        
        for (String altName : alternativeNames) {
            try {
                EquipmentSlot result = EquipmentValidator.validateSlotName(altName);
                assert result != null : "Alternative slot name '" + altName + "' should return a valid EquipmentSlot";
                System.out.println("  ✓ Alternative slot name '" + altName + "' -> " + result);
            } catch (Exception e) {
                throw new RuntimeException("Alternative slot name '" + altName + "' should be valid", e);
            }
        }
        
        // Test invalid slot names
        String[] invalidSlotNames = {"invalid", "body", "hand", "armor", ""};
        for (String invalidName : invalidSlotNames) {
            try {
                EquipmentValidator.validateSlotName(invalidName);
                throw new RuntimeException("Slot name '" + invalidName + "' should be invalid");
            } catch (InternalExpressionException e) {
                System.out.println("  ✓ Invalid slot name '" + invalidName + "' correctly rejected: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test proper error handling with descriptive messages
     * Requirements: 3.3 - Add proper error handling with descriptive messages for invalid parameters
     */
    private static void testErrorMessages() {
        System.out.println("Testing error message quality...");
        
        // Test invalid slot number with descriptive message
        try {
            EquipmentValidator.validateSlotNumber(10);
            throw new RuntimeException("Should have thrown exception for invalid slot");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            assert message.contains("Equipment slot must be between 0 and 5") : "Error message should be descriptive";
            assert message.contains("10") : "Error message should include the invalid value";
            System.out.println("  ✓ Slot number error message: " + message);
        }
        
        // Test invalid slot name with descriptive message
        try {
            EquipmentValidator.validateSlotName("invalid_slot");
            throw new RuntimeException("Should have thrown exception for invalid slot name");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            assert message.contains("Invalid equipment slot name") : "Error message should be descriptive";
            assert message.contains("invalid_slot") : "Error message should include the invalid value";
            assert message.contains("Valid slot names are") : "Error message should list valid options";
            System.out.println("  ✓ Slot name error message: " + message);
        }
        
        // Test empty slot name
        try {
            EquipmentValidator.validateSlotName("");
            throw new RuntimeException("Should have thrown exception for empty slot name");
        } catch (InternalExpressionException e) {
            String message = e.getMessage();
            assert message.contains("cannot be empty") : "Error message should mention empty input";
            System.out.println("  ✓ Empty slot name error message: " + message);
        }
    }
    
    /**
     * Test equipment slot mapping consistency
     */
    private static void testSlotMapping() {
        System.out.println("Testing slot mapping consistency...");
        
        // Verify slot number to EquipmentSlot mapping
        assert EquipmentValidator.validateSlotNumber(0) == EquipmentSlot.MAINHAND;
        assert EquipmentValidator.validateSlotNumber(1) == EquipmentSlot.FEET;
        assert EquipmentValidator.validateSlotNumber(2) == EquipmentSlot.LEGS;
        assert EquipmentValidator.validateSlotNumber(3) == EquipmentSlot.CHEST;
        assert EquipmentValidator.validateSlotNumber(4) == EquipmentSlot.HEAD;
        assert EquipmentValidator.validateSlotNumber(5) == EquipmentSlot.OFFHAND;
        
        // Verify string name to EquipmentSlot mapping
        assert EquipmentValidator.validateSlotName("head") == EquipmentSlot.HEAD;
        assert EquipmentValidator.validateSlotName("helmet") == EquipmentSlot.HEAD;
        assert EquipmentValidator.validateSlotName("chest") == EquipmentSlot.CHEST;
        assert EquipmentValidator.validateSlotName("chestplate") == EquipmentSlot.CHEST;
        assert EquipmentValidator.validateSlotName("legs") == EquipmentSlot.LEGS;
        assert EquipmentValidator.validateSlotName("leggings") == EquipmentSlot.LEGS;
        assert EquipmentValidator.validateSlotName("feet") == EquipmentSlot.FEET;
        assert EquipmentValidator.validateSlotName("boots") == EquipmentSlot.FEET;
        assert EquipmentValidator.validateSlotName("mainhand") == EquipmentSlot.MAINHAND;
        assert EquipmentValidator.validateSlotName("weapon") == EquipmentSlot.MAINHAND;
        assert EquipmentValidator.validateSlotName("offhand") == EquipmentSlot.OFFHAND;
        assert EquipmentValidator.validateSlotName("shield") == EquipmentSlot.OFFHAND;
        
        // Test reverse mapping
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.MAINHAND) == 0;
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.FEET) == 1;
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.LEGS) == 2;
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.CHEST) == 3;
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.HEAD) == 4;
        assert EquipmentValidator.getSlotNumber(EquipmentSlot.OFFHAND) == 5;
        
        System.out.println("  ✓ All slot mappings are consistent");
    }
}