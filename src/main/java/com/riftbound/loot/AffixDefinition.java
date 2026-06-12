package com.riftbound.loot;

import net.minecraft.util.RandomSource;

import java.util.List;

public record AffixDefinition(
        String id,
        String translationKey,
        AffixSlot slot,
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

    public static final AffixDefinition SQUIRES = new AffixDefinition(
            "squires", "affix.riftbound.squires", AffixSlot.PREFIX, 1,
            List.of(new AffixRoll(15, 19), new AffixRoll(16, 20))
    );
    public static final AffixDefinition HEATED = new AffixDefinition(
            "heated", "affix.riftbound.heated", AffixSlot.PREFIX, 1,
            List.of(new AffixRoll(1, 2), new AffixRoll(3, 4))
    );
    public static final AffixDefinition HEAVY = new AffixDefinition(
            "heavy", "affix.riftbound.heavy", AffixSlot.PREFIX, 1,
            List.of(new AffixRoll(40, 49))
    );

    public static final AffixDefinition OF_SKILL = new AffixDefinition(
            "of_skill", "affix.riftbound.of_skill", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(5, 7))
    );
    public static final AffixDefinition OF_NEEDLING = new AffixDefinition(
            "of_needling", "affix.riftbound.of_needling", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(10, 14))
    );
    public static final AffixDefinition OF_BRUTE = new AffixDefinition(
            "of_brute", "affix.riftbound.of_brute", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(8, 12))
    );
    public static final AffixDefinition OF_MONGOOSE = new AffixDefinition(
            "of_mongoose", "affix.riftbound.of_mongoose", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(8, 12))
    );
    public static final AffixDefinition OF_STEADINESS = new AffixDefinition(
            "of_steadiness", "affix.riftbound.of_steadiness", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(80, 130))
    );
    public static final AffixDefinition OF_SUCCESS = new AffixDefinition(
            "of_success", "affix.riftbound.of_success", AffixSlot.SUFFIX, 1,
            List.of(new AffixRoll(7, 10))
    );

    public RolledAffix roll(RandomSource random) {
        int[] rolled = new int[rolls.size()];
        for (int i = 0; i < rolls.size(); i++) {
            rolled[i] = rolls.get(i).roll(random);
        }
        return RolledAffix.ofInts(id, rolled);
    }

    public boolean canRollOn(int itemLevel) {
        return itemLevel >= minIlvl;
    }
}
