package com.riftbound.loot;



import net.minecraft.util.RandomSource;



import java.util.ArrayList;

import java.util.Collection;

import java.util.HashSet;

import java.util.List;

import java.util.Set;



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



    public record RareAffixRoll(List<RolledAffix> prefixes, List<RolledAffix> suffixes) {

    }



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

        return rollPrefixExcludingIds(random, itemLevel, Set.of());

    }



    public static RolledAffix rollSuffix(RandomSource random, int itemLevel) {

        return rollSuffixExcludingIds(random, itemLevel, Set.of());

    }



    public static RolledAffix rollPrefixExcluding(RandomSource random, int itemLevel, String excludedId) {

        Set<String> excluded = excludedId == null ? Set.of() : Set.of(excludedId);

        return rollPrefixExcludingIds(random, itemLevel, excluded);

    }



    public static RolledAffix rollSuffixExcluding(RandomSource random, int itemLevel, String excludedId) {

        Set<String> excluded = excludedId == null ? Set.of() : Set.of(excludedId);

        return rollSuffixExcludingIds(random, itemLevel, excluded);

    }



    public static RolledAffix rollPrefixExcludingIds(RandomSource random, int itemLevel, Collection<String> excludedIds) {

        List<AffixDefinition> eligible = eligibleExcluding(PREFIXES, itemLevel, excludedIds);

        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));

        return definition.roll(random);

    }



    public static RolledAffix rollSuffixExcludingIds(RandomSource random, int itemLevel, Collection<String> excludedIds) {

        List<AffixDefinition> eligible = eligibleExcluding(SUFFIXES, itemLevel, excludedIds);

        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));

        return definition.roll(random);

    }



    public static List<RolledAffix> rollPrefixes(RandomSource random, int itemLevel, int count, Collection<String> excludedIds) {

        return rollAffixes(random, itemLevel, count, PREFIXES, excludedIds);

    }



    public static List<RolledAffix> rollSuffixes(RandomSource random, int itemLevel, int count, Collection<String> excludedIds) {

        return rollAffixes(random, itemLevel, count, SUFFIXES, excludedIds);

    }



    public static RareAffixRoll rollRareAffixes(RandomSource random, int itemLevel) {
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

        List<RolledAffix> prefixes = rollPrefixes(random, itemLevel, prefixCount, Set.of());
        Set<String> usedIds = new HashSet<>();
        prefixes.forEach(affix -> usedIds.add(affix.id()));
        List<RolledAffix> suffixes = rollSuffixes(random, itemLevel, suffixCount, usedIds);
        return new RareAffixRoll(prefixes, suffixes);
    }



    private static List<RolledAffix> rollAffixes(

            RandomSource random,

            int itemLevel,

            int count,

            List<AffixDefinition> pool,

            Collection<String> excludedIds

    ) {

        List<RolledAffix> rolled = new ArrayList<>(count);

        Set<String> usedIds = new HashSet<>(excludedIds);

        for (int i = 0; i < count; i++) {

            RolledAffix affix = rollFromPoolExcludingIds(random, itemLevel, pool, usedIds);

            rolled.add(affix);

            usedIds.add(affix.id());

        }

        return rolled;

    }



    private static RolledAffix rollFromPoolExcludingIds(

            RandomSource random,

            int itemLevel,

            List<AffixDefinition> pool,

            Set<String> excludedIds

    ) {

        List<AffixDefinition> eligible = eligibleExcluding(pool, itemLevel, excludedIds);

        AffixDefinition definition = eligible.get(random.nextInt(eligible.size()));

        return definition.roll(random);

    }



    private static List<AffixDefinition> eligibleExcluding(

            List<AffixDefinition> pool,

            int itemLevel,

            Collection<String> excludedIds

    ) {

        List<AffixDefinition> eligible = pool.stream()

                .filter(definition -> definition.canRollOn(itemLevel))

                .filter(definition -> !excludedIds.contains(definition.id()))

                .toList();

        if (eligible.isEmpty()) {

            throw new IllegalStateException("No affixes eligible for item level " + itemLevel);

        }

        return eligible;

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


