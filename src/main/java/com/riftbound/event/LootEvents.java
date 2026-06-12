package com.riftbound.event;

import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootItemFactory;
import com.riftbound.registry.ModItems;
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

        RandomSource random = serverLevel.getRandom();

        if (random.nextFloat() < 0.35F) {
            int count = 1 + random.nextInt(2);
            event.getDrops().add(createDrop(entity, new ItemStack(ModItems.SHARD_DUST.get(), count)));
        }

        if (!isVanillaMob(entity)) {
            return;
        }

        float bladeRoll = random.nextFloat();
        if (bladeRoll < 0.08F) {
            ItemRarity rarity;
            if (bladeRoll < 0.01F) {
                rarity = ItemRarity.RARE;
            } else if (bladeRoll < 0.03F) {
                rarity = ItemRarity.MAGIC;
            } else {
                rarity = ItemRarity.NORMAL;
            }

            ItemStack blade = LootItemFactory.createShardBlade(random, rarity, serverLevel.registryAccess());
            event.getDrops().add(createDrop(entity, blade));
        }
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
