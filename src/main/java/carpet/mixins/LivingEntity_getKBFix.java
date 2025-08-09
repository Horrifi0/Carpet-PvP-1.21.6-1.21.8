package carpet.mixins;

import carpet.CarpetSettings;
import carpet.fakes.PlayerSwordBlockInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntity_getKBFix {

    @Inject(
            method = "getKnockback(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyKnockback(Entity entity, DamageSource damageSource, CallbackInfoReturnable<Float> cir) {
        // entity is the target (victim)
        boolean targetIsBlocking = false;
        if (CarpetSettings.swordBlockHitting && entity instanceof LivingEntity target && target instanceof PlayerSwordBlockInterface) {
            targetIsBlocking = ((PlayerSwordBlockInterface) target).carpet$getSwordBlockTicks() > 0;
        }

        float baseKnockback = (float) ((LivingEntity) (Object) this).getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        Level level = ((LivingEntity) (Object) this).level();
        float result = baseKnockback;
        if (level instanceof ServerLevel serverLevel) {
            result = EnchantmentHelper.modifyKnockback(serverLevel, ((LivingEntity) (Object) this).getMainHandItem(), entity, damageSource, baseKnockback);
        }
        if (targetIsBlocking) {
            result *= (float) CarpetSettings.swordBlockKnockbackMultiplier;
        }
        cir.setReturnValue(result);
    }
}
