package com.riftbound.registry;

import com.mojang.serialization.MapCodec;
import com.riftbound.RiftboundMod;
import com.riftbound.loot.RiftboundWorldLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RiftboundMod.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<RiftboundWorldLootModifier>> WORLD_DROP =
            LOOT_MODIFIERS.register("world_drop", () -> RiftboundWorldLootModifier.CODEC);

    private ModLootModifiers() {
    }

    public static void register(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
