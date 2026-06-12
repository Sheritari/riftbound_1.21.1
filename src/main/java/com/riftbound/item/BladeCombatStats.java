package com.riftbound.item;

import com.riftbound.RiftboundMod;
import com.riftbound.loot.AffixDefinition;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.RolledAffix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Optional;

public final class BladeCombatStats {
    public static final double MIN_BASE_DAMAGE = 5.0D;
    public static final double MAX_BASE_DAMAGE = 11.0D;
    public static final double ATTACK_SPEED = 1.45D;
    public static final float FULL_ATTACK_STRENGTH_THRESHOLD = 0.9F;
    public static final float CRIT_CHANCE = 0.05F;
    public static final float CRIT_DAMAGE_MULTIPLIER = 2.0F;
    public static final int IMPLICIT_ACCURACY_INCREASED = 40;
    private static final double VANILLA_SWORD_REACH_BLOCKS = 3.0D;
    public static final double REACH_BONUS_BLOCKS = 0.1D;
    public static final double REACH_BLOCKS = VANILLA_SWORD_REACH_BLOCKS + REACH_BONUS_BLOCKS;

    private static final double PLAYER_BASE_ATTACK_SPEED = 4.0D;

    private BladeCombatStats() {
    }

    public static ItemAttributeModifiers defaultBladeAttributes() {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(
                Attributes.ATTACK_SPEED,
                attackSpeedModifier(ATTACK_SPEED),
                EquipmentSlotGroup.MAINHAND
        );
        builder.add(
                Attributes.ENTITY_INTERACTION_RANGE,
                reachModifier(),
                EquipmentSlotGroup.MAINHAND
        );
        return builder.build();
    }

    public static AttributeModifier attackSpeedModifier(ItemStack stack) {
        return attackSpeedModifier(getAttackSpeed(stack));
    }

    public static AttributeModifier attackSpeedModifier(double attackSpeed) {
        return modifier("attack_speed", attackSpeed - PLAYER_BASE_ATTACK_SPEED);
    }

    public static AttributeModifier reachModifier() {
        return modifier("reach", REACH_BONUS_BLOCKS);
    }

    public static float rollHitDamage(RandomSource random) {
        return (float) (MIN_BASE_DAMAGE + random.nextDouble() * (MAX_BASE_DAMAGE - MIN_BASE_DAMAGE));
    }

    public static float rollPhysicalDamage(RandomSource random, ItemStack stack) {
        float damage = rollHitDamage(random);
        damage *= 1.0F + getIncreasedPhysicalPercent(stack) / 100.0F;
        damage += getFlatPhysicalBonus(stack);
        return damage;
    }

    public static float rollFireDamage(RandomSource random, ItemStack stack) {
        Optional<RolledAffix> heated = getPrefix(stack, AffixDefinition.HEATED);
        if (heated.isEmpty()) {
            return 0.0F;
        }

        int min = (int) heated.get().value(0);
        int max = (int) heated.get().value(1);
        if (max < min) {
            int swap = min;
            min = max;
            max = swap;
        }
        if (max == min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    public static float getCritChance(ItemStack stack) {
        return CRIT_CHANCE + getSuffixValue(stack, AffixDefinition.OF_NEEDLING) / 100.0F;
    }

    public static float getLifeOnKillPercent(ItemStack stack) {
        return getSuffixValue(stack, AffixDefinition.OF_SUCCESS);
    }

    public static double getAttackSpeed(ItemStack stack) {
        double increased = getSuffixValue(stack, AffixDefinition.OF_SKILL)
                + getSuffixValue(stack, AffixDefinition.OF_MONGOOSE);
        return ATTACK_SPEED * (1.0D + increased / 100.0D);
    }

    private static float getIncreasedPhysicalPercent(ItemStack stack) {
        return getPrefixValue(stack, AffixDefinition.SQUIRES, 0)
                + getPrefixValue(stack, AffixDefinition.HEAVY, 0);
    }

    private static float getFlatPhysicalBonus(ItemStack stack) {
        return getSuffixValue(stack, AffixDefinition.OF_BRUTE);
    }

    private static float getPrefixValue(ItemStack stack, AffixDefinition definition, int index) {
        return LootDataHelper.getPrefix(stack)
                .filter(affix -> affix.id().equals(definition.id()))
                .map(affix -> (float) affix.value(index))
                .orElse(0.0F);
    }

    private static float getSuffixValue(ItemStack stack, AffixDefinition definition) {
        return LootDataHelper.getSuffix(stack)
                .filter(affix -> affix.id().equals(definition.id()))
                .map(affix -> (float) affix.value())
                .orElse(0.0F);
    }

    private static Optional<RolledAffix> getPrefix(ItemStack stack, AffixDefinition definition) {
        return LootDataHelper.getPrefix(stack)
                .filter(affix -> affix.id().equals(definition.id()));
    }

    private static AttributeModifier modifier(String id, double amount) {
        return new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, id),
                amount,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

}
