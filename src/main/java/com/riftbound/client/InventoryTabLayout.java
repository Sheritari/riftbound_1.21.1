package com.riftbound.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public final class InventoryTabLayout {
    private static final int PANEL_WIDTH = 176;

    private InventoryTabLayout() {
    }

    public static int tabLeft(AbstractContainerScreen<?> screen) {
        return (screen.width - PANEL_WIDTH) / 2;
    }

    public static int tabTop(AbstractContainerScreen<?> screen) {
        if (screen instanceof CreativeModeInventoryScreen) {
            return screen.height - 136;
        }
        if (screen instanceof InventoryScreen) {
            return (screen.height - 166) / 2 - 24;
        }
        return (screen.height - screen.getYSize()) / 2 - 24;
    }
}
