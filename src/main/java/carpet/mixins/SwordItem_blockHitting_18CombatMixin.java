package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class SwordItem_blockHitting_18CombatMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void startSwordBlocking(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!CarpetSettings.swordBlockHitting) return;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ItemTags.SWORDS)) return;
        
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResult.CONSUME);
    }
    
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void setSwordUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!CarpetSettings.swordBlockHitting) return;
        if (!stack.is(ItemTags.SWORDS)) return;
        
        cir.setReturnValue(72000); // Very long duration for persistent blocking
    }
}
