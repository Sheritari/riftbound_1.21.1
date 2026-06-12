package com.riftbound.client;

import com.riftbound.menu.TransmutationMenu;
import com.riftbound.network.MenuTabPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class TransmutationScreen extends AbstractContainerScreen<TransmutationMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/hopper.png");

    public TransmutationScreen(TransmutationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("gui.riftbound.tab.inventory"), button -> {
            PacketDistributor.sendToServer(new MenuTabPayload(MenuTabPayload.Tab.INVENTORY));
        }).bounds(this.leftPos + 8, this.topPos + 4, 72, 18).build());

        Button transmutationTab = Button.builder(Component.translatable("gui.riftbound.tab.transmutation"), button -> {
        }).bounds(this.leftPos + 84, this.topPos + 4, 84, 18).build();
        transmutationTab.active = false;
        addRenderableWidget(transmutationTab);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
