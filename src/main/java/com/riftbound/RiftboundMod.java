package com.riftbound;

import com.mojang.logging.LogUtils;
import com.riftbound.event.AnvilEvents;
import com.riftbound.event.BladeAttributeEvents;
import com.riftbound.event.BladeCombatEvents;
import com.riftbound.event.ItemInstanceEvents;
import com.riftbound.event.LootEvents;
import com.riftbound.registry.ModBlockEntities;
import com.riftbound.registry.ModBlocks;
import com.riftbound.registry.ModCreativeTabs;
import com.riftbound.registry.ModItems;
import com.riftbound.network.ModNetworking;
import com.riftbound.registry.ModLootModifiers;
import com.riftbound.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(RiftboundMod.MOD_ID)
public class RiftboundMod {
    public static final String MOD_ID = "riftbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RiftboundMod(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenus.register(modEventBus);
        modEventBus.addListener(ModNetworking::registerPayloadHandlers);

        NeoForge.EVENT_BUS.register(LootEvents.class);
        NeoForge.EVENT_BUS.register(ItemInstanceEvents.class);
        NeoForge.EVENT_BUS.register(BladeAttributeEvents.class);
        NeoForge.EVENT_BUS.register(BladeCombatEvents.class);
        NeoForge.EVENT_BUS.register(AnvilEvents.class);

        LOGGER.info("Riftbound loaded");
    }
}
