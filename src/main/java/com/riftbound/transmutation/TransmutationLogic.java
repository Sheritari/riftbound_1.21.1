package com.riftbound.transmutation;

import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.LootItemFactory;
import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class TransmutationLogic {
    private TransmutationLogic() {
    }

    public static boolean canCombine(ItemStack first, ItemStack second) {
        return matchInputs(first, second) != null;
    }

    public static ItemStack getResult(ItemStack first, ItemStack second, HolderLookup.Provider registries) {
        ItemStack blade = matchInputs(first, second);
        if (blade == null || registries == null) {
            return ItemStack.EMPTY;
        }

        ItemStack dust = isDust(first) ? first : second;
        long seed = combinationSeed(dust, blade);
        return LootItemFactory.upgradeToMagic(blade, RandomSource.create(seed), registries);
    }

    private static ItemStack matchInputs(ItemStack first, ItemStack second) {
        if (first.isEmpty() || second.isEmpty()) {
            return null;
        }
        if (isDust(first) && isNormalBlade(second)) {
            return second;
        }
        if (isDust(second) && isNormalBlade(first)) {
            return first;
        }
        return null;
    }

    public static long combinationSeed(ItemStack first, ItemStack second) {
        long hashFirst = stackIdentity(first);
        long hashSecond = stackIdentity(second);
        long lower = Math.min(hashFirst, hashSecond);
        long upper = Math.max(hashFirst, hashSecond);
        return lower ^ (upper * 31L + 0x9E3779B97F4A7C15L);
    }

    public static long stackIdentity(ItemStack stack) {
        long hash = stack.getItem().hashCode();
        hash = 31L * hash + stack.getCount();

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (!tag.isEmpty()) {
                hash = 31L * hash + tag.hashCode();
            }
        }

        hash = 31L * hash + LootDataHelper.getRarity(stack).ordinal();
        hash = 31L * hash + LootDataHelper.getItemLevel(stack);
        return hash;
    }

    private static boolean isDust(ItemStack stack) {
        return stack.is(ModItems.SHARD_DUST.get());
    }

    private static boolean isNormalBlade(ItemStack stack) {
        return stack.is(ModItems.SHARD_BLADE.get()) && LootDataHelper.getRarity(stack) == ItemRarity.NORMAL;
    }
}
