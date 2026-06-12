package com.riftbound.network;

import com.riftbound.RiftboundMod;
import com.riftbound.menu.TransmutationMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(RiftboundMod.MOD_ID);

        registrar.playToServer(
                MenuTabPayload.TYPE,
                MenuTabPayload.STREAM_CODEC,
                ModNetworking::handleMenuTab
        );
    }

    private static void handleMenuTab(MenuTabPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            switch (payload.tab()) {
                case TRANSMUTATION -> serverPlayer.openMenu(new TransmutationMenuProvider());
                case INVENTORY -> serverPlayer.connection.send(new ClientboundOpenScreenPacket(
                        serverPlayer.inventoryMenu.containerId,
                        serverPlayer.inventoryMenu.getType(),
                        Component.translatable("container.crafting")
                ));
            }
        });
    }
}
