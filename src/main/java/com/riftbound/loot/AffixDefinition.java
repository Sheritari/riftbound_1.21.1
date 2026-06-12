package com.riftbound.loot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

public record AffixDefinition(
        String id,
        String translationKey,
        AffixType type,
        double minValue,
        double maxValue,
        Optional<ResourceKey<Enchantment>> enchantment,
        Optional<Integer> enchantmentLevel
) {
    public enum AffixType {
        DAMAGE,
        ATTACK_SPEED,
        ENCHANT
    }

    public static final AffixDefinition SHARP = new AffixDefinition(
            "sharp", "affix.riftbound.sharp", AffixType.DAMAGE, 1.0, 3.0, Optional.empty(), Optional.empty()
    );
    public static final AffixDefinition SWIFT = new AffixDefinition(
            "swift", "affix.riftbound.swift", AffixType.ATTACK_SPEED, 0.05, 0.15, Optional.empty(), Optional.empty()
    );
    public static final AffixDefinition EMBER = new AffixDefinition(
            "ember", "affix.riftbound.ember", AffixType.ENCHANT, 0, 0,
            Optional.of(Enchantments.FIRE_ASPECT), Optional.of(1)
    );
    public static final AffixDefinition SERRATED = new AffixDefinition(
            "serrated", "affix.riftbound.serrated", AffixType.DAMAGE, 0.5, 1.5, Optional.empty(), Optional.empty()
    );
    public static final AffixDefinition BRUTAL = new AffixDefinition(
            "brutal", "affix.riftbound.brutal", AffixType.DAMAGE, 2.0, 4.0, Optional.empty(), Optional.empty()
    );

    public void apply(ItemStack stack, int tier, double rolledValue, HolderLookup.Provider registries) {
        switch (type) {
            case DAMAGE -> addModifier(stack, Attributes.ATTACK_DAMAGE, rolledValue, id + "_damage");
            case ATTACK_SPEED -> addModifier(stack, Attributes.ATTACK_SPEED, rolledValue, id + "_speed");
            case ENCHANT -> enchantment.ifPresent(enchantKey -> {
                Holder<Enchantment> enchant = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantKey);
                ItemEnchantments current = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
                mutable.set(enchant, enchantmentLevel.orElse(1));
                stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
            });
        }
    }

    private static void addModifier(ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                    double amount, String modifierId) {
        ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        current.modifiers().forEach(entry ->
                builder.add(entry.attribute(), entry.modifier(), entry.slot())
        );
        builder.add(
                attribute,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("riftbound", modifierId),
                        amount,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND
        );
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    public double rollValue(net.minecraft.util.RandomSource random) {
        return minValue + random.nextDouble() * (maxValue - minValue);
    }
}
