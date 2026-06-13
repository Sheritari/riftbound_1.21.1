package com.riftbound.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * Maps world areas to the maximum item level that can drop or roll affixes there.
 */
public final class AreaItemLevel {
    public static final int OVERWORLD = 1;
    public static final int NETHER = 11;
    public static final int END = 21;

    private AreaItemLevel() {
    }

    public static int forEntity(LivingEntity entity) {
        return forDimension(entity.level().dimension());
    }

    public static int forLootContext(LootContext context) {
        return forDimension(context.getLevel().dimension());
    }

    public static int forDimension(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.NETHER)) {
            return NETHER;
        }
        if (dimension.equals(Level.END)) {
            return END;
        }
        return OVERWORLD;
    }

    public static int clampToArea(int itemLevel, int areaLevel) {
        return Math.min(Math.max(itemLevel, 1), areaLevel);
    }
}
