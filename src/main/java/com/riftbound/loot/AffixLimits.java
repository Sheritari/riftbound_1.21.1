package com.riftbound.loot;

import java.util.List;

public final class AffixLimits {
    public static final int MAGIC_MAX_PREFIXES = 1;
    public static final int MAGIC_MAX_SUFFIXES = 1;
    public static final int RARE_MAX_PREFIXES = 3;
    public static final int RARE_MAX_SUFFIXES = 3;
    public static final int RARE_MIN_TOTAL_AFFIXES = 3;
    public static final int RARE_MAX_TOTAL_AFFIXES = 6;

    private AffixLimits() {
    }

    public static int maxPrefixes(ItemRarity rarity) {
        return switch (rarity) {
            case NORMAL -> 0;
            case MAGIC -> MAGIC_MAX_PREFIXES;
            case RARE -> RARE_MAX_PREFIXES;
        };
    }

    public static int maxSuffixes(ItemRarity rarity) {
        return switch (rarity) {
            case NORMAL -> 0;
            case MAGIC -> MAGIC_MAX_SUFFIXES;
            case RARE -> RARE_MAX_SUFFIXES;
        };
    }

    public static boolean meetsRareRequirements(List<RolledAffix> prefixes, List<RolledAffix> suffixes) {
        if (prefixes.isEmpty() || suffixes.isEmpty()) {
            return false;
        }
        return prefixes.size() + suffixes.size() >= RARE_MIN_TOTAL_AFFIXES;
    }

    public static ItemRarity normalizeRarity(ItemRarity rarity, List<RolledAffix> prefixes, List<RolledAffix> suffixes) {
        if (rarity == ItemRarity.RARE && !meetsRareRequirements(prefixes, suffixes)) {
            return prefixes.isEmpty() && suffixes.isEmpty() ? ItemRarity.NORMAL : ItemRarity.MAGIC;
        }
        return rarity;
    }
}
