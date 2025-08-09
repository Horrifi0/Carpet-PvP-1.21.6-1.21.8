package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRenderer_swordBlockArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void carpet$forceBlockPose(AbstractClientPlayer player, HumanoidArm arm, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
    // Show pose if we are in an active S2C window OR simply holding use with a sword (consumable block)
    boolean active = SwordBlockVisuals.isActive(player) || (player.isUsingItem() && player.getUseItem().is(ItemTags.SWORDS));
    if (!active) return;
        // Map the arm being rendered to the corresponding hand for item lookup
        InteractionHand hand = (arm == player.getMainArm()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    if (player.getItemInHand(hand).is(ItemTags.SWORDS)) {
            cir.setReturnValue(HumanoidModel.ArmPose.BLOCK);
        }
    }
}
