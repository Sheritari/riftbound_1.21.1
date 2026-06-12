package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.item.ShardBladeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RiftboundMod.MOD_ID);

    public static final DeferredItem<Item> SHARD_DUST = ITEMS.registerSimpleItem(
            "shard_dust",
            new Item.Properties()
    );

    public static final DeferredItem<BlockItem> SHARD_ORE_ITEM = ITEMS.registerSimpleBlockItem(
            "shard_ore",
            ModBlocks.SHARD_ORE
    );

    public static final DeferredItem<ShardBladeItem> SHARD_BLADE = ITEMS.register(
            "shard_blade",
            () -> new ShardBladeItem(new Item.Properties().stacksTo(1))
    );

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
