package carpet.mixins;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to fix fake player critical hits in Minecraft 1.21.4+
 * 
 * In 1.21.4, there were changes to movement mechanics that prevent fake players
 * from performing critical hits. This mixin ensures that fake players can still
 * perform critical hits by overriding the onGround check.
 */
@Mixin(Player.class)
public abstract class Player_fakePlayerCritsMixin
{
    /**
     * Redirects the onGround() check in the attack method for fake players.
     * 
     * The issue in 1.21.4 is that fake players may have their onGround flag
     * incorrectly set to true even when they should be falling. This redirect
     * ensures that when a fake player is falling (velocity.y < 0), the onGround
     * check returns false, allowing critical hits to work properly.
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
            boolean isFalling = fakePlayer.getDeltaMovement().y < 0.0;
            
            // If falling, return false to allow critical hits
            if (isFalling)
            {
                return false;
            }
        }
        
        // Otherwise, use the original onGround value
        return player.onGround();
    }
}
