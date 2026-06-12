package com.riftbound.menu;

public final class TransmutationLayout {
    public static final int PANEL_WIDTH = 176;
    public static final int PANEL_HEIGHT = 166;
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    private static final int CRAFT_LEFT = 4;
    private static final int CRAFT_TOP = 6;
    private static final int CRAFT_RIGHT = 172;
    private static final int CRAFT_BOTTOM = 70;
    private static final int CRAFT_WIDTH = CRAFT_RIGHT - CRAFT_LEFT;
    private static final int CRAFT_HEIGHT = CRAFT_BOTTOM - CRAFT_TOP;

    private static final int SLOT_SIZE = 18;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 16;
    private static final int CRAFT_GAP = 10;

    private static final int CRAFT_ROW_WIDTH =
            SLOT_SIZE + CRAFT_GAP + SLOT_SIZE + CRAFT_GAP + ARROW_WIDTH + CRAFT_GAP + SLOT_SIZE;
    private static final int CRAFT_START_X = CRAFT_LEFT + (CRAFT_WIDTH - CRAFT_ROW_WIDTH) / 2;
    private static final int SLOT_Y = CRAFT_TOP + (CRAFT_HEIGHT - SLOT_SIZE) / 2;
    private static final int CRAFT_ARROW_Y = CRAFT_TOP + (CRAFT_HEIGHT - ARROW_HEIGHT) / 2;

    public static final int INPUT_1_X = CRAFT_START_X;
    public static final int INPUT_1_Y = SLOT_Y;
    public static final int INPUT_2_X = CRAFT_START_X + SLOT_SIZE + CRAFT_GAP;
    public static final int INPUT_2_Y = SLOT_Y;
    public static final int ARROW_X = INPUT_2_X + SLOT_SIZE + CRAFT_GAP;
    public static final int ARROW_Y = CRAFT_ARROW_Y;
    public static final int RESULT_X = ARROW_X + ARROW_WIDTH + CRAFT_GAP;
    public static final int RESULT_Y = SLOT_Y;

    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 84;
    public static final int HOTBAR_Y = 142;
    public static final int INVENTORY_LABEL_Y = 72;

    private TransmutationLayout() {
    }
}
