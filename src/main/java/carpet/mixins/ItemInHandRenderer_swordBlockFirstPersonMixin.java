package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
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

        float ease;
        if (holding) {
            ease = 1.0f;
        } else {
            int left = SwordBlockVisuals.remaining(player);
            ease = Math.min(1.0f, left / 6.0f);
            ease = ease * ease;
        }

        float rotY = -18.0f * ease;
        float rotX = -10.0f * ease;
        poseStack.mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(rotX), (float) Math.toRadians(rotY), 0));
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
