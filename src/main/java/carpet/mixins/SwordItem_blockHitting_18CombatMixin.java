package carpet.mixins;

import carpet.CarpetSettings;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public abstract class SwordItem_blockHitting_18CombatMixin extends Item {
	public SwordItem_blockHitting_18CombatMixin(Properties properties) { super(properties); }

	// Begin using sword on right-click, so isUsingItem() becomes true while holding use
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void carpet$startSwordUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (!CarpetSettings.swordBlockHitting) return;
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		cir.setReturnValue(InteractionResultHolder.consume(stack));
	}

	// Allow very long use duration so holding use persists
	@Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
	private void carpet$swordUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
		if (!CarpetSettings.swordBlockHitting) return;
		cir.setReturnValue(72000);
	}
}
