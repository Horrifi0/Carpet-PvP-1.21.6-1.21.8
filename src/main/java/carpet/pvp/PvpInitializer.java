package carpet.pvp;

import carpet.network.payload.SwordBlockPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import carpet.fakes.PlayerSwordBlockInterface;
import carpet.CarpetSettings;
import carpet.network.payload.SwordBlockRequestPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.core.component.DataComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class PvpInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Apply consumable components to all swords programmatically
        applySwordConsumableComponents();
        
        // Register S2C payload type for encoding on the server
        PayloadTypeRegistry.playS2C().register(SwordBlockPayload.TYPE, SwordBlockPayload.STREAM_CODEC);
        // Register C2S payload codec on the server
        PayloadTypeRegistry.playC2S().register(SwordBlockRequestPayload.TYPE, SwordBlockRequestPayload.STREAM_CODEC);

        // Handle client requests to open block window (right-click air)
        ServerPlayNetworking.registerGlobalReceiver(SwordBlockRequestPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> {
                if (!CarpetSettings.swordBlockHitting) return;
                if (player == null) return;
                if (player.getItemInHand(payload.hand()).is(ItemTags.SWORDS)) {
                    int ticks = CarpetSettings.swordBlockWindowTicks;
                    ((PlayerSwordBlockInterface) player).carpet$setSwordBlockTicks(ticks);
                    ClientboundCustomPayloadPacket msg = new ClientboundCustomPayloadPacket(new SwordBlockPayload(player.getId(), ticks));
                    for (ServerPlayer p : player.level().players()) {
                        if (p.distanceToSqr(player) < 64*64) {
                            p.connection.send(msg);
                        }
                    }
                }
            });
        });

        // Register a block attack callback (left-click on blocks)
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            var state = level.getBlockState(pos);
            // Example: punish empty-hand hits on tool-required blocks
            if (!player.isSpectator() && player.getMainHandItem().isEmpty() && state.requiresCorrectToolForDrops()) {
                if (!level.isClientSide) {
                    player.hurt(((ServerLevel) level).damageSources().generic(), 1.0F);
                }
                return InteractionResult.SUCCESS; // consume
            }
            return InteractionResult.PASS; // let vanilla and other handlers run
        });
    }
    
    /**
     * Programmatically apply consumable components to all swords using the tag system.
     * This replaces the need for individual JSON files for each sword type.
     */
    private void applySwordConsumableComponents() {
        // Wait for server to be ready before applying components
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item.getDefaultInstance().is(ItemTags.SWORDS)) {
                    applySwordConsumable(item);
                }
            }
        });
    }
    
    private void applySwordConsumable(Item sword) {
        // Create consumable component with block animation
        Consumable consumable = Consumable.builder()
            .consumeSeconds(3600.0f) // Long duration
            .animation(ItemUseAnimation.BLOCK) // Block animation like shields
            .hasConsumeParticles(false) // No particles
            .build();
            
        // Apply the component to the sword's default stack
        // Note: This approach requires access to item component modification
        // In practice, the mixin approach we're using is more reliable
        // sword.getDefaultInstance().set(DataComponents.CONSUMABLE, consumable);
    }
}
