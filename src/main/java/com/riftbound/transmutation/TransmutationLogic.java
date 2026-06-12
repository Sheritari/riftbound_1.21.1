package com.riftbound.transmutation;

import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.LootItemFactory;
import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Container;
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
        if (!canCombine(first, second) || registries == null) {
            return ItemStack.EMPTY;
        }

        long seed = combinationSeed(first, second);
        return getResultWithSeed(first, second, seed, registries);
    }

    public static ItemStack getResultWithSeed(ItemStack first, ItemStack second, long seed, HolderLookup.Provider registries) {
        ItemStack blade = matchInputs(first, second);
        if (blade == null || registries == null || seed == 0L) {
            return ItemStack.EMPTY;
        }

        RandomSource random = RandomSource.create(seed);
        ItemRarity rarity = LootDataHelper.getRarity(blade);

        if (rarity == ItemRarity.NORMAL) {
            return LootItemFactory.addSuffix(blade, random, registries);
        }
        if (rarity == ItemRarity.MAGIC && LootDataHelper.hasPrefix(blade) && !LootDataHelper.hasSuffix(blade)) {
            return LootItemFactory.addSuffixToRare(blade, random, registries);
        }
        if (LootDataHelper.hasSuffix(blade)) {
            return LootItemFactory.rerollSuffix(blade, random, registries);
        }

        return ItemStack.EMPTY;
    }

    public static void consumeInputs(Container inputContainer) {
        ItemStack first = inputContainer.getItem(0);
        ItemStack second = inputContainer.getItem(1);

        if (isDust(first)) {
            first.shrink(1);
        } else if (isDust(second)) {
            second.shrink(1);
        }

        if (isShardBlade(first)) {
            first.shrink(1);
        } else if (isShardBlade(second)) {
            second.shrink(1);
        }
    }

    private static ItemStack matchInputs(ItemStack first, ItemStack second) {
        if (first.isEmpty() || second.isEmpty()) {
            return null;
        }
        if (isDust(first) && isShardBlade(second)) {
            return second;
        }
        if (isDust(second) && isShardBlade(first)) {
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

        long instanceId = LootDataHelper.getInstanceId(stack).orElse(0L);
        if (instanceId != 0L) {
            hash = 31L * hash + instanceId;
        }

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

    private static boolean isShardBlade(ItemStack stack) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return false;
        }
        ItemRarity rarity = LootDataHelper.getRarity(stack);
        return rarity == ItemRarity.NORMAL
                || rarity == ItemRarity.MAGIC
                || rarity == ItemRarity.RARE;
    }
}
