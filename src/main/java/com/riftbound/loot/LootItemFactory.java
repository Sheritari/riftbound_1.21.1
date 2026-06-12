package com.riftbound.loot;

import com.riftbound.item.BladeCombatStats;
import com.riftbound.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class LootItemFactory {
    private LootItemFactory() {
    }

    public static ItemStack createNormalBlade() {
        ItemStack stack = new ItemStack(ModItems.SHARD_BLADE.get());
        long instanceId = RandomSource.createNewThreadLocalInstance().nextLong();
        finalizeBlade(
                stack,
                LootDataHelper.getBaseItemLevel(stack),
                Optional.empty(),
                Optional.empty(),
                instanceId
        );
        return stack;
    }

    public static ItemStack createShardBlade(RandomSource random, ItemRarity rarity, HolderLookup.Provider registries) {
        ItemStack stack = new ItemStack(ModItems.SHARD_BLADE.get());
        int itemLevel = LootDataHelper.getBaseItemLevel(stack);

        Optional<RolledAffix> prefix = Optional.empty();
        Optional<RolledAffix> suffix = Optional.empty();

        if (rarity == ItemRarity.MAGIC) {
            prefix = Optional.of(AffixPool.rollPrefix(random, itemLevel));
        } else if (rarity == ItemRarity.RARE) {
            prefix = Optional.of(AffixPool.rollPrefix(random, itemLevel));
            suffix = Optional.of(AffixPool.rollSuffix(random, itemLevel));
        }

        long instanceId = random.nextLong();
        finalizeBlade(stack, itemLevel, prefix, suffix, instanceId);
        return stack;
    }

    public static ItemStack addSuffix(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity != ItemRarity.NORMAL) {
            return ItemStack.EMPTY;
        }

        int itemLevel = LootDataHelper.getItemLevel(stack);
        long instanceId = LootDataHelper.getInstanceId(stack).orElseGet(random::nextLong);
        Optional<RolledAffix> suffix = Optional.of(AffixPool.rollSuffix(random, itemLevel));

        ItemStack result = new ItemStack(ModItems.SHARD_BLADE.get());
        finalizeBlade(result, itemLevel, Optional.empty(), suffix, instanceId);
        return result;
    }

    public static ItemStack addSuffixToRare(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity != ItemRarity.MAGIC || !LootDataHelper.hasPrefix(stack) || LootDataHelper.hasSuffix(stack)) {
            return ItemStack.EMPTY;
        }

        int itemLevel = LootDataHelper.getItemLevel(stack);
        long instanceId = LootDataHelper.getInstanceId(stack).orElseGet(random::nextLong);
        Optional<RolledAffix> prefix = LootDataHelper.getPrefix(stack);
        Optional<RolledAffix> suffix = Optional.of(AffixPool.rollSuffix(random, itemLevel));

        ItemStack result = new ItemStack(ModItems.SHARD_BLADE.get());
        finalizeBlade(result, itemLevel, prefix, suffix, instanceId);
        return result;
    }

    public static ItemStack addRandomSuffixIfMissing(ItemStack stack, RandomSource random) {
        if (!stack.is(ModItems.SHARD_BLADE.get()) || LootDataHelper.hasSuffix(stack)) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity == ItemRarity.NORMAL) {
            return addSuffix(stack, random, null);
        }
        if (rarity == ItemRarity.MAGIC && LootDataHelper.hasPrefix(stack)) {
            return addSuffixToRare(stack, random, null);
        }
        if (rarity == ItemRarity.MAGIC) {
            return addSuffix(stack, random, null);
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack rerollSuffix(ItemStack stack, RandomSource random, HolderLookup.Provider registries) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return ItemStack.EMPTY;
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        if (rarity == ItemRarity.NORMAL || !LootDataHelper.hasSuffix(stack)) {
            return ItemStack.EMPTY;
        }

        int itemLevel = LootDataHelper.getItemLevel(stack);
        long instanceId = LootDataHelper.getInstanceId(stack).orElseGet(random::nextLong);
        Optional<RolledAffix> prefix = LootDataHelper.getPrefix(stack);
        String currentSuffixId = LootDataHelper.getSuffix(stack).map(RolledAffix::id).orElse(null);
        Optional<RolledAffix> suffix = Optional.of(AffixPool.rollSuffixExcluding(random, itemLevel, currentSuffixId));

        ItemStack result = new ItemStack(ModItems.SHARD_BLADE.get());
        finalizeBlade(result, itemLevel, prefix, suffix, instanceId);
        return result;
    }

    private static void finalizeBlade(
            ItemStack stack,
            int itemLevel,
            Optional<RolledAffix> prefix,
            Optional<RolledAffix> suffix,
            long instanceId
    ) {
        ItemRarity resolvedRarity = resolveRarity(prefix, suffix);
        LootDataHelper.write(stack, resolvedRarity, itemLevel, prefix, suffix, instanceId);
        BladeCombatStats.refreshAttributes(stack);
        stack.set(DataComponents.CUSTOM_NAME, buildName(prefix, suffix));
    }

    private static Component buildName(Optional<RolledAffix> prefix, Optional<RolledAffix> suffix) {
        StringBuilder name = new StringBuilder();
        prefix.ifPresent(affix -> {
            name.append(Component.translatable("affix.riftbound." + affix.id()).getString());
            name.append(' ');
        });
        name.append(Component.translatable("item.riftbound.shard_blade").getString());
        suffix.ifPresent(affix -> {
            name.append(' ');
            name.append(Component.translatable("affix.riftbound." + affix.id()).getString());
        });
        ItemRarity rarity = resolveRarity(prefix, suffix);
        return Component.literal(name.toString()).withStyle(rarity.style());
    }

    private static ItemRarity resolveRarity(Optional<RolledAffix> prefix, Optional<RolledAffix> suffix) {
        if (prefix.isPresent() && suffix.isPresent()) {
            return ItemRarity.RARE;
        }
        if (prefix.isPresent() || suffix.isPresent()) {
            return ItemRarity.MAGIC;
        }
        return ItemRarity.NORMAL;
    }
}
