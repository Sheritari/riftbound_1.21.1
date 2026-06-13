package com.riftbound.loot;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AffixPool {
    private static final Map<String, AffixDefinition> BY_ID = new HashMap<>();
    private static final Map<ItemLootCategory, List<AffixDefinition>> PREFIXES_BY_CATEGORY = new EnumMap<>(ItemLootCategory.class);
    private static final Map<ItemLootCategory, List<AffixDefinition>> SUFFIXES_BY_CATEGORY = new EnumMap<>(ItemLootCategory.class);

    static {
        for (AffixDefinition definition : AffixDefinition.ALL) {
            register(definition);
        }
    }

    public record RareAffixRoll(List<RolledAffix> prefixes, List<RolledAffix> suffixes) {
    }

    private AffixPool() {
    }

    private static void register(AffixDefinition definition) {
        BY_ID.put(definition.id(), definition);
        Map<ItemLootCategory, List<AffixDefinition>> target = definition.slot() == AffixSlot.PREFIX
                ? PREFIXES_BY_CATEGORY
                : SUFFIXES_BY_CATEGORY;
        for (ItemLootCategory category : definition.categories()) {
            target.computeIfAbsent(category, ignored -> new ArrayList<>()).add(definition);
        }
    }

    public static AffixDefinition byId(String id) {
        return BY_ID.get(id);
    }

    public static List<AffixDefinition> prefixes(ItemLootCategory category) {
        return List.copyOf(PREFIXES_BY_CATEGORY.getOrDefault(category, List.of()));
    }

    public static List<AffixDefinition> suffixes(ItemLootCategory category) {
        return List.copyOf(SUFFIXES_BY_CATEGORY.getOrDefault(category, List.of()));
    }

    public static RolledAffix rollPrefix(RandomSource random, ItemLootCategory category, int itemLevel) {
        return rollPrefixExcludingIds(random, category, itemLevel, Set.of());
    }

    public static RolledAffix rollSuffix(RandomSource random, ItemLootCategory category, int itemLevel) {
        return rollSuffixExcludingIds(random, category, itemLevel, Set.of());
    }

    public static RolledAffix rollPrefixExcluding(RandomSource random, ItemLootCategory category, int itemLevel, String excludedId) {
        Set<String> excluded = excludedId == null ? Set.of() : Set.of(excludedId);
        return rollPrefixExcludingIds(random, category, itemLevel, excluded);
    }

    public static RolledAffix rollSuffixExcluding(RandomSource random, ItemLootCategory category, int itemLevel, String excludedId) {
        Set<String> excluded = excludedId == null ? Set.of() : Set.of(excludedId);
        return rollSuffixExcludingIds(random, category, itemLevel, excluded);
    }

    public static RolledAffix rollPrefixExcludingIds(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            Collection<String> excludedIds
    ) {
        return rollFromPoolExcludingIds(random, category, itemLevel, prefixes(category), excludedIds);
    }

    public static RolledAffix rollSuffixExcludingIds(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            Collection<String> excludedIds
    ) {
        return rollFromPoolExcludingIds(random, category, itemLevel, suffixes(category), excludedIds);
    }

    public static List<RolledAffix> rollPrefixes(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            int count,
            Collection<String> excludedIds
    ) {
        return rollAffixes(random, category, itemLevel, count, prefixes(category), excludedIds);
    }

    public static List<RolledAffix> rollSuffixes(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            int count,
            Collection<String> excludedIds
    ) {
        return rollAffixes(random, category, itemLevel, count, suffixes(category), excludedIds);
    }

    public static RareAffixRoll rollRareAffixes(RandomSource random, ItemLootCategory category, int itemLevel) {
        int total = AffixLimits.RARE_MIN_TOTAL_AFFIXES
                + random.nextInt(AffixLimits.RARE_MAX_TOTAL_AFFIXES - AffixLimits.RARE_MIN_TOTAL_AFFIXES + 1);

        int prefixCount = 1 + random.nextInt(total - 1);
        int suffixCount = total - prefixCount;

        prefixCount = Math.min(prefixCount, AffixLimits.RARE_MAX_PREFIXES);
        suffixCount = Math.min(suffixCount, AffixLimits.RARE_MAX_SUFFIXES);

        while (prefixCount + suffixCount < AffixLimits.RARE_MIN_TOTAL_AFFIXES) {
            if (prefixCount < AffixLimits.RARE_MAX_PREFIXES) {
                prefixCount++;
            } else {
                suffixCount++;
            }
        }
        while (prefixCount < 1 && suffixCount > 1) {
            prefixCount++;
            suffixCount--;
        }
        while (suffixCount < 1 && prefixCount > 1) {
            suffixCount++;
            prefixCount--;
        }

        List<RolledAffix> prefixes = rollPrefixes(random, category, itemLevel, prefixCount, Set.of());
        Set<String> usedIds = new HashSet<>();
        prefixes.forEach(affix -> usedIds.add(affix.id()));
        List<RolledAffix> suffixes = rollSuffixes(random, category, itemLevel, suffixCount, usedIds);
        return new RareAffixRoll(prefixes, suffixes);
    }

    private static List<RolledAffix> rollAffixes(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            int count,
            List<AffixDefinition> pool,
            Collection<String> excludedIds
    ) {
        List<RolledAffix> rolled = new ArrayList<>(count);
        Set<String> usedIds = new HashSet<>(excludedIds);

        for (int i = 0; i < count; i++) {
            RolledAffix affix = rollFromPoolExcludingIds(random, category, itemLevel, pool, usedIds);
            rolled.add(affix);
            usedIds.add(affix.id());
        }

        return rolled;
    }

    private static RolledAffix rollFromPoolExcludingIds(
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            List<AffixDefinition> pool,
            Collection<String> excludedIds
    ) {
        List<AffixDefinition> eligible = eligibleExcluding(pool, category, itemLevel, excludedIds);
        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));
        return definition.roll(random);
    }

    private static List<AffixDefinition> eligibleExcluding(
            List<AffixDefinition> pool,
            ItemLootCategory category,
            int itemLevel,
            Collection<String> excludedIds
    ) {
        List<AffixDefinition> eligible = pool.stream()
                .filter(definition -> definition.canRollOn(category, itemLevel))
                .filter(definition -> !excludedIds.contains(definition.id()))
                .toList();

        if (eligible.isEmpty()) {
            throw new IllegalStateException(
                    "No affixes eligible for category " + category + " at item level " + itemLevel
            );
        }

        return eligible;
    }
}
