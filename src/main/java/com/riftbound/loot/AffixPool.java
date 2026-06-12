package com.riftbound.loot;

import net.minecraft.util.RandomSource;

import java.util.List;

public final class AffixPool {
    public static final List<AffixDefinition> PREFIXES = List.of(
            AffixDefinition.SQUIRES,
            AffixDefinition.HEATED,
            AffixDefinition.HEAVY
    );

    public static final List<AffixDefinition> SUFFIXES = List.of(
            AffixDefinition.OF_SKILL,
            AffixDefinition.OF_NEEDLING,
            AffixDefinition.OF_BRUTE,
            AffixDefinition.OF_MONGOOSE,
            AffixDefinition.OF_STEADINESS,
            AffixDefinition.OF_SUCCESS
    );

    private AffixPool() {
    }

    public static AffixDefinition byId(String id) {
        AffixDefinition prefix = findIn(PREFIXES, id);
        if (prefix != null) {
            return prefix;
        }
        return findIn(SUFFIXES, id);
    }

    private static AffixDefinition findIn(List<AffixDefinition> pool, String id) {
        for (AffixDefinition definition : pool) {
            if (definition.id().equals(id)) {
                return definition;
            }
        }
        return null;
    }

    public static RolledAffix rollPrefix(RandomSource random, int itemLevel) {
        List<AffixDefinition> eligible = eligible(PREFIXES, itemLevel);
        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));
        return definition.roll(random);
    }

    public static RolledAffix rollSuffix(RandomSource random, int itemLevel) {
        List<AffixDefinition> eligible = eligible(SUFFIXES, itemLevel);
        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));
        return definition.roll(random);
    }

    public static RolledAffix rollSuffixExcluding(RandomSource random, int itemLevel, String excludedId) {
        List<AffixDefinition> eligible = eligible(SUFFIXES, itemLevel);
        if (excludedId == null || eligible.size() <= 1) {
            return rollSuffix(random, itemLevel);
        }

        AffixDefinition rolled;
        int attempts = 0;
        do {
            rolled = eligible.get(random.nextInt(eligible.size()));
            attempts++;
        } while (rolled.id().equals(excludedId) && attempts < 16);

        return rolled.roll(random);
    }

    private static List<AffixDefinition> eligible(List<AffixDefinition> pool, int itemLevel) {
        List<AffixDefinition> eligible = pool.stream()
                .filter(definition -> definition.canRollOn(itemLevel))
                .toList();
        if (eligible.isEmpty()) {
            throw new IllegalStateException("No affixes eligible for item level " + itemLevel);
        }
        return eligible;
    }
}
