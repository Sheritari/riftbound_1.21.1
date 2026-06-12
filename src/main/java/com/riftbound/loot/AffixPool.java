package com.riftbound.loot;

import net.minecraft.util.RandomSource;

import java.util.List;

public final class AffixPool {
    public static final List<AffixDefinition> ACT_ONE = List.of(
            AffixDefinition.SHARP,
            AffixDefinition.SWIFT,
            AffixDefinition.EMBER,
            AffixDefinition.SERRATED,
            AffixDefinition.BRUTAL
    );

    private AffixPool() {
    }

    public static AffixDefinition byId(String id) {
        for (AffixDefinition definition : ACT_ONE) {
            if (definition.id().equals(id)) {
                return definition;
            }
        }
        return null;
    }

    public static AffixDefinition roll(RandomSource random) {
        return ACT_ONE.get(random.nextInt(ACT_ONE.size()));
    }

    public static AffixDefinition rollExcluding(RandomSource random, String excludedId) {
        if (excludedId == null || ACT_ONE.size() <= 1) {
            return roll(random);
        }

        AffixDefinition rolled;
        int attempts = 0;
        do {
            rolled = roll(random);
            attempts++;
        } while (rolled.id().equals(excludedId) && attempts < 16);

        return rolled;
    }
}
