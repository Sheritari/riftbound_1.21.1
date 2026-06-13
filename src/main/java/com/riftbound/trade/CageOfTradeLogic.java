package com.riftbound.trade;

import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.registry.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class CageOfTradeLogic {
    public static final int INPUT_SLOTS = 15;

    private CageOfTradeLogic() {
    }

    public static int countMagicItems(Container container) {
        int count = 0;
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty() && isMagicTradeItem(stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean canPlaceInInput(ItemStack stack) {
        return !stack.isEmpty() && LootDataHelper.isModItem(stack);
    }

    public static boolean isMagicTradeItem(ItemStack stack) {
        return !stack.isEmpty() && LootDataHelper.getRarity(stack) == ItemRarity.MAGIC;
    }

    public static ItemStack getOutputPreview(Container container) {
        int count = countMagicItems(container);
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(ModItems.RESONANT_SHARD.get(), count);
    }

    public static void consumeInputs(Container container) {
        for (int slot = 0; slot < INPUT_SLOTS; slot++) {
            ItemStack stack = container.getItem(slot);
            if (isMagicTradeItem(stack)) {
                container.setItem(slot, ItemStack.EMPTY);
            }
        }
        container.setChanged();
    }
}
