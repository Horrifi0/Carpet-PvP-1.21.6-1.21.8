package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 1.21.8: Keep only size limit patches; the ignored-block injection no longer matches at bootstrap time.
 * The "ignored block" behavior will be revisited against new saveStructure/fillFromWorld pipeline.
 */
@Mixin(StructureBlockEntity.class)
public abstract class StructureBlockEntity_limitsMixin
{
    @ModifyConstant(
            method = "loadAdditional",
            constant = @Constant(intValue = StructureBlockEntity.MAX_SIZE_PER_AXIS)
    )
    private int positiveLimit(int original) {
        return CarpetSettings.structureBlockLimit;
    }

    @ModifyConstant(
            method = "loadAdditional",
            constant = @Constant(intValue = -StructureBlockEntity.MAX_SIZE_PER_AXIS)
    )
    private int negativeLimit(int original) {
        return -CarpetSettings.structureBlockLimit;
    }
}
