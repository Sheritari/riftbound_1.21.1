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

    public static AffixDefinition roll(RandomSource random) {
        return ACT_ONE.get(random.nextInt(ACT_ONE.size()));
    }
}
