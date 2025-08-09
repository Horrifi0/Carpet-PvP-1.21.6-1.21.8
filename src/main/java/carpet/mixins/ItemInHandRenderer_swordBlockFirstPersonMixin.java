package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRenderer_swordBlockFirstPersonMixin {
    @Unique private boolean carpet$pushed;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void carpet$blockPoseStart(
            AbstractClientPlayer player,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float equipProgress,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        this.carpet$pushed = false;
        if (player == null) return;
        if (!SwordBlockVisuals.isActive(player)) return;
        if (stack.isEmpty() || !stack.is(ItemTags.SWORDS)) return;

        boolean right = (hand == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.RIGHT)
                || (hand == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.LEFT);

        poseStack.pushPose();
        this.carpet$pushed = true;
        // Mild defensive pose to keep item in view
        float rotY = right ? -35.0f : 35.0f;
        float rotX = -20.0f;
        float transX = right ? -0.12f : 0.12f;
        float transY = 0.02f; // slight up
        float transZ = 0.0f;  // avoid moving into/away from camera to prevent clipping
        poseStack.translate(transX, transY, transZ);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), 0));
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void carpet$blockPoseEnd(
            AbstractClientPlayer player,
            float partialTick,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float equipProgress,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            CallbackInfo ci
    ) {
        if (this.carpet$pushed) {
            poseStack.popPose();
            this.carpet$pushed = false;
        }
    }
}
