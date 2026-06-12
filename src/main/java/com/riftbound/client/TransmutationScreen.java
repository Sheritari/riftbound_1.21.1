package com.riftbound.client;

import com.riftbound.RiftboundMod;
import com.riftbound.menu.TransmutationLayout;
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
            ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, "textures/gui/transmutation.png");

    public TransmutationScreen(TransmutationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = TransmutationLayout.PANEL_WIDTH;
        this.imageHeight = TransmutationLayout.PANEL_HEIGHT;
        this.titleLabelX = -1000;
        this.titleLabelY = -1000;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = TransmutationLayout.INVENTORY_LABEL_Y;
    }

    @Override
    protected void init() {
        super.init();
        int tabLeft = InventoryTabLayout.tabLeft(this);
        int tabTop = this.topPos - 24;

        addRenderableWidget(Button.builder(Component.translatable("gui.riftbound.tab.inventory"), button ->
                PacketDistributor.sendToServer(new MenuTabPayload(MenuTabPayload.Tab.INVENTORY))
        ).bounds(tabLeft, tabTop, 72, 20).build());

        Button craftTab = Button.builder(Component.translatable("gui.riftbound.tab.craft"), button -> {
        }).bounds(tabLeft + 76, tabTop, 84, 20).build();
        craftTab.active = false;
        addRenderableWidget(craftTab);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(
                TEXTURE,
                this.leftPos,
                this.topPos,
                0,
                0,
                this.imageWidth,
                this.imageHeight,
                TransmutationLayout.TEXTURE_WIDTH,
                TransmutationLayout.TEXTURE_HEIGHT
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
