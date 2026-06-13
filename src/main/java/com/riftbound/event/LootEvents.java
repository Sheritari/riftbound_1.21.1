package com.riftbound.event;

import com.riftbound.loot.WorldLootRoller;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public final class LootEvents {
    private LootEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (!(entity instanceof Monster)) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!isVanillaMob(entity)) {
            return;
        }

        RandomSource random = serverLevel.getRandom();
        WorldLootRoller.rollModDrop(random, serverLevel.registryAccess())
                .ifPresent(stack -> event.getDrops().add(createDrop(entity, stack)));
    }

    private static boolean isVanillaMob(LivingEntity entity) {
        return entity.getType().builtInRegistryHolder()
                .unwrapKey()
                .map(key -> "minecraft".equals(key.location().getNamespace()))
                .orElse(false);
    }

    private static ItemEntity createDrop(LivingEntity entity, ItemStack stack) {
        return new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
    }
}
