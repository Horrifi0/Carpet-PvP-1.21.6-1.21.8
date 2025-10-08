package carpet.mixins;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWindCharge.class)
public abstract class WindCharge_consistentMomentumMixin extends AbstractHurtingProjectile
{
    protected WindCharge_consistentMomentumMixin(EntityType<? extends AbstractHurtingProjectile> entityType, Level level)
    {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void normalizeWindChargeMomentum(CallbackInfo ci)
    {
        // Ensure consistent momentum for wind charges thrown by bots
        // This runs every tick to maintain consistent physics
        Entity owner = this.getOwner();
        
        if (owner instanceof Player player && player instanceof EntityPlayerMPFake)
        {
            Vec3 velocity = this.getDeltaMovement();
            
            // Only adjust if the wind charge is still ascending or near the owner
            double distToOwner = this.distanceToSqr(owner);
            if (distToOwner < 25.0 && velocity.y > 0) // Within 5 blocks and moving upward
            {
                // Normalize to ensure consistent upward momentum
                // Target: 1.0 blocks/tick upward, reduced horizontal
                double currentUpward = velocity.y;
                
                if (currentUpward < 0.8) // If upward velocity is too low
                {
                    // Boost upward momentum, reduce horizontal
                    this.setDeltaMovement(velocity.x * 0.7, 1.0, velocity.z * 0.7);
                }
            }
        }
    }
}
