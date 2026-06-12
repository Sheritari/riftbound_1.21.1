package com.riftbound.client;

import com.riftbound.RiftboundMod;
import com.riftbound.network.MenuTabPayload;
import com.riftbound.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = RiftboundMod.MOD_ID, value = Dist.CLIENT)
public final class RiftboundClient {
    private RiftboundClient() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen) && !(event.getScreen() instanceof CreativeModeInventoryScreen)) {
            return;
        }

        int left = event.getScreen().width / 2 - 88;
        int top = event.getScreen().height / 2 - 94;

        Button inventoryTab = Button.builder(Component.translatable("gui.riftbound.tab.inventory"), button -> {
        }).bounds(left, top - 22, 72, 18).build();
        inventoryTab.active = false;
        event.addListener(inventoryTab);

        event.addListener(Button.builder(Component.translatable("gui.riftbound.tab.transmutation"), button -> {
            if (Minecraft.getInstance().player != null) {
                PacketDistributor.sendToServer(new MenuTabPayload(MenuTabPayload.Tab.TRANSMUTATION));
            }
        }).bounds(left + 76, top - 22, 84, 18).build());
    }
}

@EventBusSubscriber(modid = RiftboundMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
final class RiftboundClientModBus {
    private RiftboundClientModBus() {
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.TRANSMUTATION.get(), TransmutationScreen::new);
    }
}
