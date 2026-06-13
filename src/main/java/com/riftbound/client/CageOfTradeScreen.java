package com.riftbound.client;

import com.riftbound.RiftboundMod;
import com.riftbound.menu.CageOfTradeLayout;
import com.riftbound.menu.CageOfTradeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CageOfTradeScreen extends AbstractContainerScreen<CageOfTradeMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, "textures/gui/cage_of_trade.png");

    public CageOfTradeScreen(CageOfTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = CageOfTradeLayout.PANEL_WIDTH;
        this.imageHeight = CageOfTradeLayout.PANEL_HEIGHT;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = CageOfTradeLayout.INVENTORY_LABEL_Y;
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
                CageOfTradeLayout.TEXTURE_WIDTH,
                CageOfTradeLayout.TEXTURE_HEIGHT
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
