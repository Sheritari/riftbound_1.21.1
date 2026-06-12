package com.riftbound.event;

import com.riftbound.item.BladeCombatStats;
import com.riftbound.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public final class BladeAttributeEvents {
    private BladeAttributeEvents() {
    }

    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        event.replaceModifier(
                Attributes.ATTACK_SPEED,
                BladeCombatStats.attackSpeedModifier(stack),
                EquipmentSlotGroup.MAINHAND
        );
        event.replaceModifier(
                Attributes.ENTITY_INTERACTION_RANGE,
                BladeCombatStats.reachModifier(),
                EquipmentSlotGroup.MAINHAND
        );
    }
}
