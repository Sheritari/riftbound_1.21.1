package com.riftbound.loot;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public enum ItemRarity {
    NORMAL("normal", ChatFormatting.WHITE),
    MAGIC("magic", ChatFormatting.BLUE),
    RARE("rare", ChatFormatting.YELLOW);

    private final String id;
    private final ChatFormatting color;

    ItemRarity(String id, ChatFormatting color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public Style style() {
        return Style.EMPTY.withColor(color);
    }

    public static ItemRarity fromId(String id) {
        for (ItemRarity rarity : values()) {
            if (rarity.id.equals(id)) {
                return rarity;
            }
        }
        return NORMAL;
    }
}
