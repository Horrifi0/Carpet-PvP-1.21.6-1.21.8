package carpet.mixins;

import carpet.CarpetSettings;
import carpet.fakes.PlayerSwordBlockInterface;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImpl_swordBlockMarkerMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleUseItem", at = @At("HEAD"))
    private void markSwordBlock(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (!CarpetSettings.swordBlockHitting) return;
        InteractionHand hand = packet.getHand();
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.is(ItemTags.SWORDS)) {
            ((PlayerSwordBlockInterface) player).carpet$setSwordBlockTicks(CarpetSettings.swordBlockWindowTicks);
        }
    }
}
