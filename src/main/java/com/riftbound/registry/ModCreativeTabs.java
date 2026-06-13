package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.loot.LootItemFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RiftboundMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.riftbound"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> LootItemFactory.createNormalBlade())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.SHARD_DUST.get());
                        output.accept(ModItems.SHARD_STONE.get());
                        output.accept(ModItems.RESONANT_SHARD.get());
                        output.accept(ModItems.ORB_OF_RESONANT.get());
                        output.accept(ModBlocks.CAGE_OF_TRADE.get());
                        output.accept(ModBlocks.SHARD_ORE.get());
                        output.accept(LootItemFactory.createNormalBlade());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }
}
