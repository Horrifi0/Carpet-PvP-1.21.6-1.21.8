package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import net.minecraft.client.Minecraft;
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
    private void carpet$blockHitStart(
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
        if (hand != InteractionHand.MAIN_HAND) return; // main-hand only
        if (stack.isEmpty() || !stack.is(ItemTags.SWORDS)) return;

        boolean holding = Minecraft.getInstance().options.keyUse.isDown();
        if (!holding && !SwordBlockVisuals.isActive(player)) return;

        poseStack.pushPose();
        this.carpet$pushed = true;

        float ease = holding ? 1.0f : Math.min(1.0f, SwordBlockVisuals.remaining(player) / 6.0f);
        ease = ease * ease;

        // Roll-only in-place tilt (Z axis). Strong left tilt (>45°) toward center.
        float baseAngle = 60.0f;
        float dir = (player.getMainArm() == HumanoidArm.RIGHT) ? 1.0f : -1.0f;
        float roll = dir * baseAngle * ease;
        poseStack.mulPose(new Quaternionf().rotationXYZ(0f, 0f, (float) Math.toRadians(roll)));

        // Lower slightly to keep the tip height near original; pure negative Y, no X/Z shift.
        float drop = 1.08f * ease; // tune if still too high/low
        poseStack.translate(0.0F, -drop, 0.0F);
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void carpet$blockHitEnd(
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
