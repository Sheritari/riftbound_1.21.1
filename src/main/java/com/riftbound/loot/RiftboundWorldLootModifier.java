package com.riftbound.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public class RiftboundWorldLootModifier extends LootModifier {
    public static final MapCodec<RiftboundWorldLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, RiftboundWorldLootModifier::new));

    public RiftboundWorldLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends net.neoforged.neoforge.common.loot.IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!isVanillaChestLootTable(context)) {
            return generatedLoot;
        }

        int areaItemLevel = AreaItemLevel.forLootContext(context);
        WorldLootRoller.rollModDrop(context.getRandom(), context.getLevel().registryAccess(), areaItemLevel)
                .ifPresent(generatedLoot::add);
        return generatedLoot;
    }

    private static boolean isVanillaChestLootTable(LootContext context) {
        ResourceLocation tableId = context.getQueriedLootTableId();
        return tableId != null
                && "minecraft".equals(tableId.getNamespace())
                && tableId.getPath().startsWith("chests/");
    }
}
