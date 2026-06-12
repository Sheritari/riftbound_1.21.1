package com.riftbound.network;

import com.riftbound.menu.TransmutationMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                MenuTabPayload.TYPE,
                MenuTabPayload.STREAM_CODEC,
                ModNetworking::handleMenuTabServer
        );
    }

    private static void handleMenuTabServer(MenuTabPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            switch (payload.tab()) {
                case TRANSMUTATION -> serverPlayer.openMenu(new TransmutationMenuProvider());
                case INVENTORY -> {
                    serverPlayer.closeContainer();
                    PacketDistributor.sendToPlayer(serverPlayer, new SyncScreenPayload(MenuTabPayload.Tab.INVENTORY));
                }
            }
        });
    }
}
