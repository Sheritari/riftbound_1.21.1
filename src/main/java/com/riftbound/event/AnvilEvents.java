package com.riftbound.event;

import com.riftbound.loot.LootDataHelper;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public final class AnvilEvents {
    private AnvilEvents() {
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (!LootDataHelper.isModItem(event.getLeft())) {
            return;
        }
        if (event.getRight().is(Items.ENCHANTED_BOOK)) {
            event.setCanceled(true);
        }
    }
}
