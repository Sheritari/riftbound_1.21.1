package com.riftbound.client;

import com.riftbound.RiftboundMod;
import com.riftbound.network.MenuTabPayload;
import com.riftbound.network.SyncScreenPayload;
import com.riftbound.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = RiftboundMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RiftboundClientMod {
    private RiftboundClientMod() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CAGE_OF_TRADE.get(), CageOfTradeScreen::new);
        event.register(ModMenus.TRANSMUTATION.get(), TransmutationScreen::new);
    }

    @SubscribeEvent
    public static void registerClientPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                SyncScreenPayload.TYPE,
                SyncScreenPayload.STREAM_CODEC,
                RiftboundClientMod::handleSyncScreen
        );
    }

    private static void handleSyncScreen(SyncScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || payload.tab() != MenuTabPayload.Tab.INVENTORY) {
                return;
            }

            minecraft.setScreen(new InventoryScreen(minecraft.player));
        });
    }
}
