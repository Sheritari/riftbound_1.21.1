package com.riftbound.loot;

import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
public final class LootItemFactory {
    private LootItemFactory() {
    }

    public static ItemStack createShardBlade(RandomSource random, int itemLevel, ItemRarity rarity, HolderLookup.Provider registries) {
        ItemStack stack = new ItemStack(ModItems.SHARD_BLADE.get());
        List<RolledAffix> affixes = new ArrayList<>();

        if (rarity == ItemRarity.MAGIC) {
            AffixDefinition affix = AffixPool.roll(random);
            double value = affix.rollValue(random);
            affix.apply(stack, itemLevel, value, registries);
            affixes.add(new RolledAffix(affix.id(), value));
        }

        LootDataHelper.write(stack, rarity, itemLevel, affixes);
        stack.set(DataComponents.CUSTOM_NAME, buildName(rarity, affixes));
        return stack;
    }

    public static ItemStack upgradeToMagic(ItemStack stack, long seed, HolderLookup.Provider registries) {
        return upgradeToMagic(stack, RandomSource.create(seed), registries);
    }

    public static ItemStack upgradeToMagic(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return stack;
        }
        if (LootDataHelper.getRarity(stack) != ItemRarity.NORMAL) {
            return stack;
        }

        ItemStack result = stack.copy();
        int itemLevel = LootDataHelper.getItemLevel(result);

        AffixDefinition affix = AffixPool.roll(random);
        double value = affix.rollValue(random);
        affix.apply(result, itemLevel, value, registries);

        List<RolledAffix> affixes = List.of(new RolledAffix(affix.id(), value));
        LootDataHelper.write(result, ItemRarity.MAGIC, itemLevel, affixes);
        result.set(DataComponents.CUSTOM_NAME, buildName(ItemRarity.MAGIC, affixes));
        return result;
    }

    private static Component buildName(ItemRarity rarity, List<RolledAffix> affixes) {
        StringBuilder name = new StringBuilder();
        if (!affixes.isEmpty()) {
            name.append(Component.translatable("affix.riftbound." + affixes.getFirst().id()).getString());
            name.append(' ');
        }
        name.append(Component.translatable("item.riftbound.shard_blade").getString());
        return Component.literal(name.toString()).withStyle(rarity.style());
    }
}
