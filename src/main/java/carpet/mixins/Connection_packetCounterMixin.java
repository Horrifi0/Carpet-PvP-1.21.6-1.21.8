package carpet.mixins;

import carpet.fakes.ClientConnectionInterface;
import carpet.logging.logHelpers.PacketCounter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class Connection_packetCounterMixin implements ClientConnectionInterface
{
    // Add to the packet counter whenever a packet is received.
    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void packetInCount(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci)
    {
        PacketCounter.totalIn++;
    }
    
    // Add to the packet counter whenever a packet is sent.
    // 1.21.8: Connection#send signature changed; HEAD callback here takes only (Packet, CallbackInfo)
    @Inject(method = "send", at = @At("HEAD"))
    private void packetOutCount(final Packet<?> packet, final CallbackInfo ci)
    {
        PacketCounter.totalOut++;
    }

    @Override
    @Accessor //Compat with adventure-platform-fabric
    public abstract void setChannel(Channel channel);
}
