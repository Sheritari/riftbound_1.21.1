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
    public static final float FULL_ATTACK_STRENGTH_THRESHOLD = 0.9F;
    private static final double VANILLA_SWORD_REACH_BLOCKS = 3.0D;
    public static final double REACH_BONUS_BLOCKS = 0.1D;
    public static final double REACH_BLOCKS = VANILLA_SWORD_REACH_BLOCKS + REACH_BONUS_BLOCKS;

    private static final double PLAYER_BASE_ATTACK_SPEED = 4.0D;

    private BladeCombatStats() {
    }

    public static float rollHitDamage(RandomSource random) {
        return (float) (MIN_BASE_DAMAGE + random.nextDouble() * (MAX_BASE_DAMAGE - MIN_BASE_DAMAGE));
    }

    public static float getAffixDamageBonus(ItemStack stack) {
        return (float) sumAffixValues(stack, AffixDefinition.AffixType.DAMAGE);
    }

    public static double getAffixAttackSpeedBonus(ItemStack stack) {
        return sumAffixValues(stack, AffixDefinition.AffixType.ATTACK_SPEED);
    }

    private static double sumAffixValues(ItemStack stack, AffixDefinition.AffixType type) {
        double total = 0.0D;
        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = AffixPool.byId(affix.id());
            if (definition != null && definition.type() == type) {
                total += affix.value();
            }
        }
        return total;
    }

    public static void ensureAttributes(ItemStack stack, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS) || needsAttributeRefresh(stack)) {
            refreshAttributes(stack, registries);
        }
    }

    private static boolean needsAttributeRefresh(ItemStack stack) {
        return hasSplitAttributeModifiers(stack) || hasStaleReachModifier(stack);
    }

    private static boolean hasStaleReachModifier(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return false;
        }
        return modifiers.modifiers().stream()
                .filter(entry -> entry.attribute().is(Attributes.ENTITY_INTERACTION_RANGE))
                .anyMatch(entry -> Math.abs(entry.modifier().amount() - REACH_BONUS_BLOCKS) > 0.001D);
    }

    private static boolean hasSplitAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return false;
        }
        return modifiers.modifiers().stream()
                .filter(entry -> entry.attribute().is(Attributes.ATTACK_SPEED))
                .count() > 1;
    }

    public static void refreshAttributes(ItemStack stack, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        double attackSpeedModifier = ATTACK_SPEED - PLAYER_BASE_ATTACK_SPEED + getAffixAttackSpeedBonus(stack);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(
                Attributes.ATTACK_SPEED,
                modifier("attack_speed", attackSpeedModifier),
                EquipmentSlotGroup.MAINHAND
        );
        builder.add(
                Attributes.ENTITY_INTERACTION_RANGE,
                modifier("reach", REACH_BONUS_BLOCKS),
                EquipmentSlotGroup.MAINHAND
        );
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());

        int itemLevel = LootDataHelper.getItemLevel(stack);
        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = AffixPool.byId(affix.id());
            if (definition != null && definition.type() == AffixDefinition.AffixType.ENCHANT) {
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
