package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.item.RiftboundBlockItem;
import com.riftbound.item.RiftboundItem;
import com.riftbound.item.ShardBladeItem;
import com.riftbound.item.ShardStoneItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RiftboundMod.MOD_ID);

    public static final DeferredItem<Item> SHARD_DUST = ITEMS.register(
            "shard_dust",
            () -> new RiftboundItem(new Item.Properties())
    );

    public static final DeferredItem<Item> SHARD_STONE = ITEMS.register(
            "shard_stone",
            () -> new ShardStoneItem(new Item.Properties())
    );

    public static final DeferredItem<RiftboundBlockItem> SHARD_ORE_ITEM = ITEMS.register(
            "shard_ore",
            () -> new RiftboundBlockItem(ModBlocks.SHARD_ORE.get(), new Item.Properties())
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
