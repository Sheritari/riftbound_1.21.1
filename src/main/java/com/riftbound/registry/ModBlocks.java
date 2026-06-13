package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.block.CageOfTradeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RiftboundMod.MOD_ID);

    public static final DeferredBlock<Block> CAGE_OF_TRADE = BLOCKS.register(
            "cage_of_trade",
            () -> new CageOfTradeBlock(CageOfTradeBlock.blockProperties())
    );

    public static final DeferredBlock<Block> SHARD_ORE = BLOCKS.registerSimpleBlock(
            "shard_ore",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST)
    );

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
