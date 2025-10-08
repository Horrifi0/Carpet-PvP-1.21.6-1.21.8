package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class Player_attackCooldown_18CombatMixin {
    @Inject(method = "getAttackStrengthScale", at = @At("HEAD"), cancellable = true)
    private void carpet$alwaysFullStrength(float baseTime, CallbackInfoReturnable<Float> cir) {
        if (CarpetSettings.spamClickCombat) {
            cir.setReturnValue(1.0F);
        }
    }
}
