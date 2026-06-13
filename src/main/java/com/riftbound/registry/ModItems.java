package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.item.BladeCombatStats;
import com.riftbound.item.RiftboundBlockItem;
import com.riftbound.item.OrbOfResonantItem;
import com.riftbound.item.ResonantShardItem;
import com.riftbound.item.ShardBladeItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RiftboundMod.MOD_ID);

    public static final DeferredItem<ResonantShardItem> RESONANT_SHARD = ITEMS.register(
            "resonant_shard",
            () -> new ResonantShardItem(new Item.Properties().stacksTo(ResonantShardItem.MAX_STACK_SIZE))
    );

    public static final DeferredItem<OrbOfResonantItem> ORB_OF_RESONANT = ITEMS.register(
            "orb_of_resonant",
            () -> new OrbOfResonantItem(new Item.Properties().stacksTo(OrbOfResonantItem.MAX_STACK_SIZE))
    );

    public static final DeferredItem<RiftboundBlockItem> CAGE_OF_TRADE_ITEM = ITEMS.register(
            "cage_of_trade",
            () -> new RiftboundBlockItem(ModBlocks.CAGE_OF_TRADE.get(), new Item.Properties())
    );

    public static final DeferredItem<RiftboundBlockItem> SHARD_ORE_ITEM = ITEMS.register(
            "shard_ore",
            () -> new RiftboundBlockItem(ModBlocks.SHARD_ORE.get(), new Item.Properties())
    );

    public static final DeferredItem<ShardBladeItem> SHARD_BLADE = ITEMS.register(
            "shard_blade",
            () -> new ShardBladeItem(new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.ATTRIBUTE_MODIFIERS, BladeCombatStats.defaultBladeAttributes()))
    );

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
