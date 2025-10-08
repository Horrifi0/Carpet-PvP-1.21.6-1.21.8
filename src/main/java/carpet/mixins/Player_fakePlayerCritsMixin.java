package carpet.mixins;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.entity.Entity;

/**
 * Mixin to fix fake player critical hits in Minecraft 1.21.4+
 * 
 * In 1.21.4, there were changes to movement mechanics that prevent fake players
 * from performing critical hits. This mixin ensures that fake players can still
 * perform critical hits by setting fallDistance when they're falling.
 */
@Mixin(Player.class)
public abstract class Player_fakePlayerCritsMixin
{
    /**
     * Inject before the attack to ensure fake players have proper fallDistance set
     */
    @Inject(method = "attack", at = @At("HEAD"))
    private void ensureFakePlayerFallDistance(Entity target, CallbackInfo ci)
    {
        Player player = (Player)(Object)this;
        if (player instanceof EntityPlayerMPFake fakePlayer)
        {
            // If fake player is falling (negative Y velocity and not on ground), ensure fallDistance is set
            if (fakePlayer.getDeltaMovement().y < 0.0 && !fakePlayer.onGround())
            {
                // Set a minimum fall distance to enable crits
                if (fakePlayer.fallDistance <= 0.0F)
                {
                    fakePlayer.fallDistance = 0.1F;
                }
            }
        }
    }
    
    /**
     * Redirects the onGround() check in the attack method for fake players.
     */
    @Redirect(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onGround()Z",
                    ordinal = 0
            )
    )
    private boolean fixFakePlayerOnGroundCheck(Player player)
    {
        // If this is a fake player and they're falling, ensure onGround returns false
        if (player instanceof EntityPlayerMPFake fakePlayer)
        {
            // Check if the fake player is falling
            if (fakePlayer.getDeltaMovement().y < 0.0)
            {
                return false;
            }
        }
        
        // Otherwise, use the original onGround value
        return player.onGround();
    }
}
