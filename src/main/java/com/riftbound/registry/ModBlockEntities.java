package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.block.entity.CageOfTradeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RiftboundMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CageOfTradeBlockEntity>> CAGE_OF_TRADE =
            BLOCK_ENTITIES.register("cage_of_trade", () -> BlockEntityType.Builder
                    .of(CageOfTradeBlockEntity::new, ModBlocks.CAGE_OF_TRADE.get())
                    .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
