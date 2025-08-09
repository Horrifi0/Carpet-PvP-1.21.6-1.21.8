package carpet.pvp;

import carpet.network.payload.SwordBlockPayload;
import carpet.client.SwordBlockVisuals;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.AbstractClientPlayer;
import carpet.network.payload.SwordBlockRequestPayload;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PvpClientInitializer implements ClientModInitializer {
    private static int swordBlockRequestCooldown = 0; // ticks

    @Override
    public void onInitializeClient() {
        // Register S2C custom payload codec on client
        PayloadTypeRegistry.playS2C().register(SwordBlockPayload.TYPE, SwordBlockPayload.STREAM_CODEC);

        // Register C2S request payload codec on client
        PayloadTypeRegistry.playC2S().register(SwordBlockRequestPayload.TYPE, SwordBlockRequestPayload.STREAM_CODEC);

        // Handle incoming block-hit payloads
        ClientPlayNetworking.registerGlobalReceiver(SwordBlockPayload.TYPE, (payload, context) -> {
            var client = context.client();
            client.execute(() -> {
                var level = client.level;
                if (level == null) return;
                var e = level.getEntity(payload.entityId());
                if (e instanceof AbstractClientPlayer p) {
                    SwordBlockVisuals.activate(p, payload.ticks());
                }
            });
        });

        // Client tick: send C2S request while use key held with a sword (throttled)
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;
            var options = client.options;

            if (swordBlockRequestCooldown > 0) swordBlockRequestCooldown--;

            if (options.keyUse.isDown()) {
                // prefer main hand, then offhand
                InteractionHand hand = InteractionHand.MAIN_HAND;
                ItemStack stack = client.player.getItemInHand(hand);
                if (stack.isEmpty() || !stack.is(ItemTags.SWORDS)) {
                    hand = InteractionHand.OFF_HAND;
                    stack = client.player.getItemInHand(hand);
                }
                if (!stack.isEmpty() && stack.is(ItemTags.SWORDS)) {
                    if (swordBlockRequestCooldown == 0) {
                        ClientPlayNetworking.send(new SwordBlockRequestPayload(hand));
                        swordBlockRequestCooldown = 4; // refresh window periodically while held
                    }
                }
            }
        });
    }
}
