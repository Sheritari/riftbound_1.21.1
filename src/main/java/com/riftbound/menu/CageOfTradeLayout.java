package com.riftbound.menu;

public final class CageOfTradeLayout {
    public static final int PANEL_WIDTH = 176;
    public static final int PANEL_HEIGHT = 166;
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    public static final int INPUT_COLUMNS = 5;
    public static final int INPUT_ROWS = 3;
    public static final int INPUT_SLOTS = INPUT_COLUMNS * INPUT_ROWS;

    public static final int OUTPUT_COLUMNS = 2;
    public static final int OUTPUT_ROWS = 3;
    public static final int OUTPUT_SLOTS = OUTPUT_COLUMNS * OUTPUT_ROWS;
    public static final int OUTPUT_START_COLUMN = 7;

    public static final int CONTAINER_LEFT = 8;
    public static final int CONTAINER_TOP = 18;
    public static final int SLOT_SIZE = 18;

    public static final int ARROW_X = CONTAINER_LEFT + INPUT_COLUMNS * SLOT_SIZE + 4;
    public static final int ARROW_Y = CONTAINER_TOP + SLOT_SIZE + 1;
    public static final int ARROW_WIDTH = 24;
    public static final int ARROW_HEIGHT = 17;

    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 84;
    public static final int HOTBAR_Y = 142;
    public static final int INVENTORY_LABEL_Y = 72;

    public static int inputSlotX(int column) {
        return CONTAINER_LEFT + column * SLOT_SIZE;
    }

    public static int inputSlotY(int row) {
        return CONTAINER_TOP + row * SLOT_SIZE;
    }

    public static int outputSlotX(int column) {
        return CONTAINER_LEFT + (OUTPUT_START_COLUMN + column) * SLOT_SIZE;
    }

    public static int outputSlotY(int row) {
        return CONTAINER_TOP + row * SLOT_SIZE;
    }

    private CageOfTradeLayout() {
    }
}
