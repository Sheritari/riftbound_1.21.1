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
    private static final ResourceLocation CHEST_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, "textures/gui/cage_of_trade_arrow.png");

    public CageOfTradeScreen(CageOfTradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = CageOfTradeLayout.PANEL_WIDTH;
        this.imageHeight = CageOfTradeLayout.PANEL_HEIGHT;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = CageOfTradeLayout.INVENTORY_LABEL_Y;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.blit(CHEST_TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blit(
                ARROW_TEXTURE,
                left + CageOfTradeLayout.ARROW_X,
                top + CageOfTradeLayout.ARROW_Y,
                0,
                0,
                CageOfTradeLayout.ARROW_WIDTH,
                CageOfTradeLayout.ARROW_HEIGHT,
                CageOfTradeLayout.ARROW_WIDTH,
                CageOfTradeLayout.ARROW_HEIGHT
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
