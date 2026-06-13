package com.riftbound.loot;

import java.util.Set;

import net.minecraft.util.RandomSource;

import java.util.List;

public record AffixDefinition(
        String id,
        String translationKey,
        AffixSlot slot,
        Set<ItemLootCategory> categories,
        int minIlvl,
        List<AffixRoll> rolls
) {
    public record AffixRoll(int min, int max) {
        public int roll(RandomSource random) {
            if (max <= min) {
                return min;
            }
            return min + random.nextInt(max - min + 1);
        }
    }

    public static final AffixDefinition SQUIRES = swordPrefix(
            "squires", "affix.riftbound.squires", 1,
            List.of(new AffixRoll(15, 19), new AffixRoll(16, 20))
    );
    public static final AffixDefinition HEATED = swordPrefix(
            "heated", "affix.riftbound.heated", 1,
            List.of(new AffixRoll(1, 2), new AffixRoll(3, 4))
    );
    public static final AffixDefinition HEAVY = swordPrefix(
            "heavy", "affix.riftbound.heavy", 1,
            List.of(new AffixRoll(40, 49))
    );
    public static final AffixDefinition GLINTING = swordPrefix(
            "glinting", "affix.riftbound.glinting", 2,
            List.of(new AffixRoll(2, 3))
    );
    public static final AffixDefinition SERRATED = swordPrefix(
            "serrated", "affix.riftbound.serrated", 11,
            List.of(new AffixRoll(50, 64))
    );

    public static final AffixDefinition OF_SKILL = swordSuffix(
            "of_skill", "affix.riftbound.of_skill", 1,
            List.of(new AffixRoll(5, 7))
    );
    public static final AffixDefinition OF_NEEDLING = swordSuffix(
            "of_needling", "affix.riftbound.of_needling", 1,
            List.of(new AffixRoll(10, 14))
    );
    public static final AffixDefinition OF_BRUTE = swordSuffix(
            "of_brute", "affix.riftbound.of_brute", 1,
            List.of(new AffixRoll(8, 12))
    );
    public static final AffixDefinition OF_MONGOOSE = swordSuffix(
            "of_mongoose", "affix.riftbound.of_mongoose", 1,
            List.of(new AffixRoll(8, 12))
    );
    public static final AffixDefinition OF_STEADINESS = swordSuffix(
            "of_steadiness", "affix.riftbound.of_steadiness", 1,
            List.of(new AffixRoll(80, 130))
    );
    public static final AffixDefinition OF_SUCCESS = swordSuffix(
            "of_success", "affix.riftbound.of_success", 1,
            List.of(new AffixRoll(7, 10))
    );

    /** Helmet prefix stub — rolls once helmet items exist. */
    public static final AffixDefinition LACQUERED = new AffixDefinition(
            "lacquered", "affix.riftbound.lacquered", AffixSlot.PREFIX,
            Set.of(ItemLootCategory.HELMET_ARMOUR), 1,
            List.of(new AffixRoll(3, 10))
    );

    public static final List<AffixDefinition> ALL = List.of(
            SQUIRES, HEATED, HEAVY, GLINTING, SERRATED,
            OF_SKILL, OF_NEEDLING, OF_BRUTE, OF_MONGOOSE, OF_STEADINESS, OF_SUCCESS,
            LACQUERED
    );

    private static AffixDefinition swordPrefix(String id, String key, int minIlvl, List<AffixRoll> rolls) {
        return new AffixDefinition(id, key, AffixSlot.PREFIX, Set.of(ItemLootCategory.ONE_HAND_SWORD), minIlvl, rolls);
    }

    private static AffixDefinition swordSuffix(String id, String key, int minIlvl, List<AffixRoll> rolls) {
        return new AffixDefinition(id, key, AffixSlot.SUFFIX, Set.of(ItemLootCategory.ONE_HAND_SWORD), minIlvl, rolls);
    }

    public RolledAffix roll(RandomSource random) {
        int[] rolled = new int[rolls.size()];
        for (int i = 0; i < rolls.size(); i++) {
            rolled[i] = rolls.get(i).roll(random);
        }
        return RolledAffix.ofInts(id, rolled);
    }

    public boolean canRollOn(ItemLootCategory category, int itemLevel) {
        return categories.contains(category) && itemLevel >= minIlvl;
    }
}
