package com.riftbound.loot;

import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class WorldLootRoller {
    private static final int MIN_DROP_CHANCE_PERCENT = 8;
    private static final int MAX_DROP_CHANCE_PERCENT = 16;
    private static final float RARE_CHANCE = 0.03F;
    private static final float MAGIC_CHANCE = 0.135F;
    private static final int NORMAL_POOL_SIZE = 2;

    private WorldLootRoller() {
    }

    public static Optional<ItemStack> rollModDrop(RandomSource random, HolderLookup.Provider registries) {
        int chancePercent = random.nextInt(MIN_DROP_CHANCE_PERCENT, MAX_DROP_CHANCE_PERCENT + 1);
        if (random.nextInt(100) >= chancePercent) {
            return Optional.empty();
        }

        ItemRarity rarity = rollRarity(random);
        return Optional.of(createDrop(random, rarity, registries));
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

    private static ItemStack createDrop(RandomSource random, ItemRarity rarity, HolderLookup.Provider registries) {
        return switch (rarity) {
            case NORMAL -> rollNormalPool(random);
            case MAGIC -> LootItemFactory.createShardBlade(random, ItemRarity.MAGIC, registries);
            case RARE -> LootItemFactory.createShardBlade(random, ItemRarity.RARE, registries);
        };
    }

    private static ItemStack rollNormalPool(RandomSource random) {
        return switch (random.nextInt(NORMAL_POOL_SIZE)) {
            case 0 -> new ItemStack(ModItems.ORB_OF_RESONANT.get());
            default -> LootItemFactory.createNormalBlade();
        };
    }
}
