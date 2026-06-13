package com.riftbound.loot;

import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * World loot entries capped by area item level. Extend as new droppable items are added.
 */
public final class LootDropRegistry {
    private static final List<LootDropEntry> NORMAL_POOL = List.of(
            LootDropEntry.always((random, areaLevel, registries) -> new ItemStack(ModItems.ORB_OF_RESONANT.get())),
            LootDropEntry.always((random, areaLevel, registries) -> LootItemFactory.createNormalBlade(areaLevel))
    );

    private LootDropRegistry() {
    }

    public static ItemStack rollNormal(RandomSource random, int areaLevel, HolderLookup.Provider registries) {
        List<LootDropEntry> eligible = NORMAL_POOL.stream()
                .filter(entry -> entry.canDropAt(areaLevel))
                .toList();
        if (eligible.isEmpty()) {
            return new ItemStack(ModItems.ORB_OF_RESONANT.get());
        }
        return eligible.get(random.nextInt(eligible.size())).create(random, areaLevel, registries);
    }

    @FunctionalInterface
    public interface DropFactory {
        ItemStack create(RandomSource random, int areaLevel, HolderLookup.Provider registries);
    }

    public record LootDropEntry(int minAreaLevel, DropFactory factory) {
        public static LootDropEntry always(DropFactory factory) {
            return new LootDropEntry(AreaItemLevel.OVERWORLD, factory);
        }

        public static LootDropEntry fromArea(int minAreaLevel, DropFactory factory) {
            return new LootDropEntry(minAreaLevel, factory);
        }

        public boolean canDropAt(int areaLevel) {
            return areaLevel >= minAreaLevel;
        }

        public ItemStack create(RandomSource random, int areaLevel, HolderLookup.Provider registries) {
            return factory.create(random, areaLevel, registries);
        }
    }
}
