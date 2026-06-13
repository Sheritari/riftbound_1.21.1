package com.riftbound.loot;

import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class LootItemFactory {
    private LootItemFactory() {
    }

    public static ItemStack createNormalBlade() {
        return createNormalBlade(1);
    }

    public static ItemStack createNormalBlade(int itemLevel) {
        ItemStack stack = new ItemStack(ModItems.SHARD_BLADE.get());
        long instanceId = RandomSource.createNewThreadLocalInstance().nextLong();
        finalizeBlade(
                stack,
                Math.max(1, itemLevel),
                ItemRarity.NORMAL,
                List.of(),
                List.of(),
                instanceId,
                RandomSource.createNewThreadLocalInstance(),
                Optional.empty()
        );
        return stack;
    }

    public static ItemStack createShardBlade(RandomSource random, ItemRarity rarity, HolderLookup.Provider registries) {
        return createShardBlade(random, rarity, registries, 1);
    }

    public static ItemStack createShardBlade(
            RandomSource random,
            ItemRarity rarity,
            HolderLookup.Provider registries,
            int itemLevel
    ) {
        ItemStack stack = new ItemStack(ModItems.SHARD_BLADE.get());
        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        int resolvedLevel = Math.max(1, itemLevel);

        List<RolledAffix> prefixes = List.of();
        List<RolledAffix> suffixes = List.of();

        if (rarity == ItemRarity.MAGIC) {
            if (random.nextBoolean()) {
                prefixes = List.of(AffixPool.rollPrefix(random, category, resolvedLevel));
            } else {
                suffixes = List.of(AffixPool.rollSuffix(random, category, resolvedLevel));
            }
        } else if (rarity == ItemRarity.RARE) {
            AffixPool.RareAffixRoll rareAffixes = AffixPool.rollRareAffixes(random, category, resolvedLevel);
            prefixes = rareAffixes.prefixes();
            suffixes = rareAffixes.suffixes();
        }

        long instanceId = random.nextLong();
        finalizeBlade(stack, resolvedLevel, rarity, prefixes, suffixes, instanceId, random, Optional.empty());
        return stack;
    }

    public static void refreshDisplayName(ItemStack stack) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        List<RolledAffix> prefixes = LootDataHelper.getPrefixes(stack);
        List<RolledAffix> suffixes = LootDataHelper.getSuffixes(stack);
        Optional<RareNameRoll> rareName = LootDataHelper.getRareName(stack);

        if (rarity == ItemRarity.NORMAL && prefixes.isEmpty() && suffixes.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_NAME);
            return;
        }

        stack.set(DataComponents.CUSTOM_NAME, buildDisplayName(rarity, prefixes, suffixes, rareName));
    }

    public static ItemStack applyPrefixCatalyst(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!LootDataHelper.canReceiveAffixes(stack)) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity == ItemRarity.NORMAL) {
            return applyNewPrefix(stack, random, ItemRarity.MAGIC, List.of(), LootDataHelper.getSuffixes(stack));
        }
        if (rarity == ItemRarity.MAGIC || rarity == ItemRarity.RARE) {
            return applyPrefixCatalystToAffixed(stack, random, rarity);
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack applySuffixCatalyst(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!LootDataHelper.canReceiveAffixes(stack)) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity == ItemRarity.NORMAL) {
            return applyNewSuffix(stack, random, ItemRarity.MAGIC, LootDataHelper.getPrefixes(stack), List.of());
        }
        if (rarity == ItemRarity.MAGIC || rarity == ItemRarity.RARE) {
            return applySuffixCatalystToAffixed(stack, random, rarity);
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack applyResonantOrbCatalyst(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!LootDataHelper.isModEquipment(stack)) {
            return ItemStack.EMPTY;
        }

        if (LootDataHelper.getRarity(stack) != ItemRarity.NORMAL) {
            return ItemStack.EMPTY;
        }

        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        int itemLevel = LootDataHelper.getItemLevel(stack);
        List<RolledAffix> prefixes = List.of();
        List<RolledAffix> suffixes = List.of();

        if (random.nextInt(10) == 0) {
            prefixes = List.of(AffixPool.rollPrefix(random, category, itemLevel));
            suffixes = List.of(AffixPool.rollSuffix(random, category, itemLevel));
        } else if (random.nextBoolean()) {
            prefixes = List.of(AffixPool.rollPrefix(random, category, itemLevel));
        } else {
            suffixes = List.of(AffixPool.rollSuffix(random, category, itemLevel));
        }

        return buildResult(stack, random, ItemRarity.MAGIC, prefixes, suffixes);
    }

    private static ItemStack applyPrefixCatalystToAffixed(ItemStack stack, RandomSource random, ItemRarity rarity) {
        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        List<RolledAffix> prefixes = new ArrayList<>(LootDataHelper.getPrefixes(stack));
        List<RolledAffix> suffixes = LootDataHelper.getSuffixes(stack);
        int maxPrefixes = AffixLimits.maxPrefixes(rarity);
        int itemLevel = LootDataHelper.getItemLevel(stack);

        if (prefixes.isEmpty()) {
            prefixes = List.of(AffixPool.rollPrefix(random, category, itemLevel));
        } else if (prefixes.size() < maxPrefixes) {
            prefixes = appendAffix(prefixes, AffixPool.rollPrefixExcludingIds(random, category, itemLevel, affixIds(prefixes)));
        } else {
            prefixes = rerollAffix(prefixes, random.nextInt(prefixes.size()), random, category, itemLevel, true);
        }

        return buildResult(stack, random, rarity, prefixes, suffixes);
    }

    private static ItemStack applySuffixCatalystToAffixed(ItemStack stack, RandomSource random, ItemRarity rarity) {
        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        List<RolledAffix> prefixes = LootDataHelper.getPrefixes(stack);
        List<RolledAffix> suffixes = new ArrayList<>(LootDataHelper.getSuffixes(stack));
        int maxSuffixes = AffixLimits.maxSuffixes(rarity);
        int itemLevel = LootDataHelper.getItemLevel(stack);

        if (suffixes.isEmpty()) {
            suffixes = List.of(AffixPool.rollSuffix(random, category, itemLevel));
        } else if (suffixes.size() < maxSuffixes) {
            suffixes = appendAffix(suffixes, AffixPool.rollSuffixExcludingIds(random, category, itemLevel, affixIds(suffixes)));
        } else {
            suffixes = rerollAffix(suffixes, random.nextInt(suffixes.size()), random, category, itemLevel, false);
        }

        return buildResult(stack, random, rarity, prefixes, suffixes);
    }

    private static ItemStack applyNewPrefix(
            ItemStack stack,
            RandomSource random,
            ItemRarity rarity,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes
    ) {
        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        int itemLevel = LootDataHelper.getItemLevel(stack);
        List<RolledAffix> updatedPrefixes = appendAffix(prefixes, AffixPool.rollPrefix(random, category, itemLevel));
        return buildResult(stack, random, rarity, updatedPrefixes, suffixes);
    }

    private static ItemStack applyNewSuffix(
            ItemStack stack,
            RandomSource random,
            ItemRarity rarity,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes
    ) {
        ItemLootCategory category = LootDataHelper.getLootCategory(stack);
        int itemLevel = LootDataHelper.getItemLevel(stack);
        List<RolledAffix> updatedSuffixes = appendAffix(suffixes, AffixPool.rollSuffix(random, category, itemLevel));
        return buildResult(stack, random, rarity, prefixes, updatedSuffixes);
    }

    private static List<RolledAffix> appendAffix(List<RolledAffix> affixes, RolledAffix affix) {
        List<RolledAffix> updated = new ArrayList<>(affixes);
        updated.add(affix);
        return updated;
    }

    private static List<RolledAffix> rerollAffix(
            List<RolledAffix> affixes,
            int index,
            RandomSource random,
            ItemLootCategory category,
            int itemLevel,
            boolean prefix
    ) {
        List<RolledAffix> updated = new ArrayList<>(affixes);
        Set<String> excludedIds = new HashSet<>(affixIds(updated));
        excludedIds.remove(updated.get(index).id());
        RolledAffix rerolled = prefix
                ? AffixPool.rollPrefixExcludingIds(random, category, itemLevel, excludedIds)
                : AffixPool.rollSuffixExcludingIds(random, category, itemLevel, excludedIds);
        updated.set(index, rerolled);
        return updated;
    }

    private static Set<String> affixIds(List<RolledAffix> affixes) {
        Set<String> ids = new HashSet<>();
        for (RolledAffix affix : affixes) {
            ids.add(affix.id());
        }
        return ids;
    }

    private static ItemStack buildResult(
            ItemStack source,
            RandomSource random,
            ItemRarity rarity,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes
    ) {
        ItemStack result = new ItemStack(source.getItem());

        long instanceId = LootDataHelper.getInstanceId(source).orElseGet(random::nextLong);

        finalizeBlade(
                result,
                LootDataHelper.getItemLevel(source),
                rarity,
                trim(prefixes, AffixLimits.maxPrefixes(rarity)),
                trim(suffixes, AffixLimits.maxSuffixes(rarity)),
                instanceId,
                random,
                LootDataHelper.getRareName(source)
        );

        return result;
    }

    private static List<RolledAffix> trim(List<RolledAffix> affixes, int maxCount) {
        if (affixes.size() <= maxCount) {
            return List.copyOf(affixes);
        }
        return List.copyOf(affixes.subList(0, maxCount));
    }

    private static void finalizeBlade(
            ItemStack stack,
            int itemLevel,
            ItemRarity rarity,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes,
            long instanceId,
            RandomSource random,
            Optional<RareNameRoll> inheritedRareName
    ) {
        ItemRarity resolvedRarity = AffixLimits.normalizeRarity(rarity, prefixes, suffixes);
        Optional<RareNameRoll> rareName = Optional.empty();
        if (resolvedRarity == ItemRarity.RARE) {
            rareName = inheritedRareName
                    .or(() -> Optional.of(RareNameGenerator.rollSwordName(random)));
        }

        LootDataHelper.write(stack, resolvedRarity, itemLevel, prefixes, suffixes, instanceId, rareName);
        if (resolvedRarity == ItemRarity.NORMAL && prefixes.isEmpty() && suffixes.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_NAME);
        } else {
            stack.set(DataComponents.CUSTOM_NAME, buildDisplayName(resolvedRarity, prefixes, suffixes, rareName));
        }
    }

    private static Component buildDisplayName(
            ItemRarity rarity,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes,
            Optional<RareNameRoll> rareName
    ) {
        if (rarity == ItemRarity.RARE) {
            MutableComponent name = rareName
                    .map(RareNameGenerator::toComponent)
                    .orElseGet(() -> Component.translatable("rare_name.riftbound.fallback"));
            return name.withStyle(ItemRarity.RARE.style());
        }

        boolean hasPrefix = !prefixes.isEmpty();
        boolean hasSuffix = !suffixes.isEmpty();
        Component base = Component.translatable("item.riftbound.shard_blade.magic");

        if (hasPrefix && hasSuffix) {
            Component prefixPart = Component.translatable(prefixAffixNameKey(prefixes.getFirst()));
            Component suffixPart = Component.translatable(suffixAffixNameKey(suffixes.getFirst()));
            return Component.translatable(
                    "magic_name.riftbound.shard_blade.both",
                    prefixPart,
                    base,
                    suffixPart
            ).withStyle(rarity.style());
        }

        if (hasPrefix) {
            Component prefixPart = Component.translatable(prefixAffixNameKey(prefixes.getFirst()));
            return Component.translatable(
                    "magic_name.riftbound.shard_blade.prefix_only",
                    prefixPart,
                    base,
                    prefixPart
            ).withStyle(rarity.style());
        }

        if (hasSuffix) {
            Component suffixPart = Component.translatable(suffixAffixNameKey(suffixes.getFirst()));
            return Component.translatable(
                    "magic_name.riftbound.shard_blade.suffix_only",
                    suffixPart,
                    base,
                    suffixPart
            ).withStyle(rarity.style());
        }

        return Component.translatable("item.riftbound.shard_blade").withStyle(rarity.style());
    }

    private static String prefixAffixNameKey(RolledAffix affix) {
        return "affix.riftbound." + affix.id() + ".name_arg1";
    }

    private static String suffixAffixNameKey(RolledAffix affix) {
        return "affix.riftbound." + affix.id() + ".name_arg3";
    }
}
