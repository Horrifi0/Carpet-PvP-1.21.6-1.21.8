package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class AbstractClientPlayer_swordBlockPoseMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void injectBlockPose(LivingEntity living, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(living instanceof AbstractClientPlayer p)) return;
        if (!SwordBlockVisuals.isActive(p)) return;
        ItemStack main = p.getMainHandItem();
        if (!main.is(ItemTags.SWORDS)) return;
        HumanoidModel<?> self = (HumanoidModel<?>) (Object) this;
        if (p.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT) {
            self.rightArm.xRot = (float) (-Math.PI / 2.2f);
            self.rightArm.yRot = (float) (-Math.PI / 10f);
        } else {
            self.leftArm.xRot = (float) (-Math.PI / 2.2f);
            self.leftArm.yRot = (float) (Math.PI / 10f);
        }
    }
}
