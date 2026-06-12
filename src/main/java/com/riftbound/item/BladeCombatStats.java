package com.riftbound.item;

import com.riftbound.RiftboundMod;
import com.riftbound.loot.AffixDefinition;
import com.riftbound.loot.AffixPool;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.RolledAffix;
import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class BladeCombatStats {
    public static final double MIN_BASE_DAMAGE = 5.0D;
    public static final double MAX_BASE_DAMAGE = 11.0D;
    public static final double ATTACK_SPEED = 1.45D;
    public static final double REACH_BLOCKS = 1.1D;

    private static final double PLAYER_BASE_ATTACK_SPEED = 4.0D;
    private static final double PLAYER_BASE_REACH = 3.0D;

    private BladeCombatStats() {
    }

    public static float rollHitDamage(RandomSource random) {
        return (float) (MIN_BASE_DAMAGE + random.nextDouble() * (MAX_BASE_DAMAGE - MIN_BASE_DAMAGE));
    }

    public static float getAffixDamageBonus(ItemStack stack) {
        float bonus = 0.0F;
        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = AffixPool.byId(affix.id());
            if (definition != null && definition.type() == AffixDefinition.AffixType.DAMAGE) {
                bonus += (float) affix.value();
            }
        }
        return bonus;
    }

    public static void ensureAttributes(ItemStack stack, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            refreshAttributes(stack, registries);
        }
    }

    public static void refreshAttributes(ItemStack stack, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(
                Attributes.ATTACK_SPEED,
                modifier("base_attack_speed", ATTACK_SPEED - PLAYER_BASE_ATTACK_SPEED),
                EquipmentSlotGroup.MAINHAND
        );
        builder.add(
                Attributes.ENTITY_INTERACTION_RANGE,
                modifier("base_reach", REACH_BLOCKS - PLAYER_BASE_REACH),
                EquipmentSlotGroup.MAINHAND
        );
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());

        int itemLevel = LootDataHelper.getItemLevel(stack);
        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = AffixPool.byId(affix.id());
            if (definition != null && definition.type() != AffixDefinition.AffixType.DAMAGE) {
                definition.apply(stack, itemLevel, affix.value(), registries);
            }
        }
    }

    private static AttributeModifier modifier(String id, double amount) {
        return new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, id),
                amount,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
