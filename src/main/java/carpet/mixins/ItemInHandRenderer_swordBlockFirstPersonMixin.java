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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRenderer_swordBlockFirstPersonMixin {
    @Shadow private net.minecraft.client.Minecraft minecraft;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void carpet$blockPose(
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
        if (player == null) return;
        if (!SwordBlockVisuals.isActive(player)) return;
        if (stack.isEmpty() || !stack.is(ItemTags.SWORDS)) return;

        boolean right = (hand == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.RIGHT)
                || (hand == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.LEFT);

        // Raise the sword defensively: rotate and translate the held item up and inward
        float rotY = right ? -50.0f : 50.0f;
        float rotX = -35.0f;
        float transX = right ? -0.25f : 0.25f;
        float transY = -0.2f; // up
        float transZ = -0.3f; // toward camera
        poseStack.translate(transX, transY, transZ);
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), 0));
    }
}
