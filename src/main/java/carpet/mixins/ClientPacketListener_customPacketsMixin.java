package carpet.mixins;

import carpet.client.SwordBlockVisuals;
import carpet.network.CarpetClient;
import carpet.network.ClientNetworkHandler;
import carpet.network.payload.SwordBlockPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListener_customPacketsMixin extends ClientCommonPacketListenerImpl
{

    protected ClientPacketListener_customPacketsMixin(final Minecraft minecraft, final Connection connection, final CommonListenerCookie commonListenerCookie)
    {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onGameJoined(ClientboundLoginPacket packet, CallbackInfo info)
    {
        CarpetClient.gameJoined( minecraft.player);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        SwordBlockVisuals.tick();
    }

    @Inject(method = "handleUnknownCustomPayload", at = @At(
            value = "HEAD"
            ), cancellable = true)
    private void onOnCustomPayload(CustomPacketPayload packet, CallbackInfo ci)
    {
        if (packet instanceof CarpetClient.CarpetPayload cpp)
        {
            ClientNetworkHandler.onServerData(cpp.data(), minecraft.player);
            ci.cancel();
            return;
        }
        if (packet instanceof SwordBlockPayload sb) {
            Entity e = minecraft.level.getEntity(sb.entityId());
            if (e instanceof AbstractClientPlayer p) {
                SwordBlockVisuals.activate(p, sb.ticks());
            }
            ci.cancel();
        }
    }

}
