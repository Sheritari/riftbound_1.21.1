package com.riftbound.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class WorldLootRoller {
    private static final int MIN_DROP_CHANCE_PERCENT = 8;
    private static final int MAX_DROP_CHANCE_PERCENT = 16;
    private static final float RARE_CHANCE = 0.03F;
    private static final float MAGIC_CHANCE = 0.135F;

    private WorldLootRoller() {
    }

    public static Optional<ItemStack> rollModDrop(RandomSource random, HolderLookup.Provider registries) {
        return rollModDrop(random, registries, AreaItemLevel.OVERWORLD);
    }

    public static Optional<ItemStack> rollModDrop(RandomSource random, HolderLookup.Provider registries, int areaItemLevel) {
        int chancePercent = random.nextInt(MIN_DROP_CHANCE_PERCENT, MAX_DROP_CHANCE_PERCENT + 1);
        if (random.nextInt(100) >= chancePercent) {
            return Optional.empty();
        }

        ItemRarity rarity = rollRarity(random);
        return Optional.of(createDrop(random, rarity, registries, areaItemLevel));
    }

    private static ItemRarity rollRarity(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < RARE_CHANCE) {
            return ItemRarity.RARE;
        }
        if (roll < RARE_CHANCE + MAGIC_CHANCE) {
            return ItemRarity.MAGIC;
        }
        return ItemRarity.NORMAL;
    }

    private static ItemStack createDrop(
            RandomSource random,
            ItemRarity rarity,
            HolderLookup.Provider registries,
            int areaItemLevel
    ) {
        int itemLevel = Math.max(1, areaItemLevel);
        return switch (rarity) {
            case NORMAL -> LootDropRegistry.rollNormal(random, areaItemLevel, registries);
            case MAGIC -> LootItemFactory.createShardBlade(random, ItemRarity.MAGIC, registries, itemLevel);
            case RARE -> LootItemFactory.createShardBlade(random, ItemRarity.RARE, registries, itemLevel);
        };
    }
}
