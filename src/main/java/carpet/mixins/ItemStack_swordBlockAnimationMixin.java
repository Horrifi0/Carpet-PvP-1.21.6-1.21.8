package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStack_swordBlockAnimationMixin {

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void getSwordBlockAnimation(CallbackInfoReturnable<ItemUseAnimation> cir) {
        if (!CarpetSettings.swordBlockHitting) return;
        
        ItemStack stack = (ItemStack)(Object)this;
        if (!stack.isEmpty() && stack.is(ItemTags.SWORDS)) {
            cir.setReturnValue(ItemUseAnimation.BLOCK);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void setSwordUseDuration(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!CarpetSettings.swordBlockHitting) return;
        
        ItemStack stack = (ItemStack)(Object)this;
        if (!stack.isEmpty() && stack.is(ItemTags.SWORDS)) {
            cir.setReturnValue(72000); // Very long duration like shields
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void startSwordBlocking(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!CarpetSettings.swordBlockHitting) return;
        
        ItemStack stack = (ItemStack)(Object)this;
        if (!stack.isEmpty() && stack.is(ItemTags.SWORDS)) {
            player.startUsingItem(hand);
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
}
