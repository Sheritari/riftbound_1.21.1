package com.riftbound.loot;

import com.riftbound.RiftboundMod;
import com.riftbound.item.ItemBaseLevelProvider;
import com.riftbound.item.ItemLootProfile;
import com.riftbound.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class LootDataHelper {
    private static final String TAG_RARITY = "Rarity";
    private static final String TAG_ILVL = "Ilvl";
    private static final String TAG_PREFIX = "Prefix";
    private static final String TAG_SUFFIX = "Suffix";
    private static final String TAG_PREFIXES = "Prefixes";
    private static final String TAG_SUFFIXES = "Suffixes";
    private static final String TAG_AFFIXES = "Affixes";
    private static final String TAG_AFFIX_ID = "Id";
    private static final String TAG_AFFIX_VALUE = "Value";
    private static final String TAG_AFFIX_VALUES = "Values";
    private static final String TAG_INSTANCE_ID = "InstanceId";
    private static final String TAG_RARE_NAME = "RareName";

    private LootDataHelper() {
    }

    public static ItemRarity getRarity(ItemStack stack) {
        return readTag(stack).map(tag -> ItemRarity.fromId(tag.getString(TAG_RARITY))).orElse(ItemRarity.NORMAL);
    }

    public static int getItemLevel(ItemStack stack) {
        return readTag(stack)
                .map(tag -> {
                    if (!tag.contains(TAG_ILVL, Tag.TAG_INT)) {
                        return getBaseItemLevel(stack);
                    }
                    int level = tag.getInt(TAG_ILVL);
                    return level > 0 ? level : getBaseItemLevel(stack);
                })
                .orElseGet(() -> getBaseItemLevel(stack));
    }

    public static int getBaseItemLevel(ItemStack stack) {
        if (stack.getItem() instanceof ItemBaseLevelProvider provider) {
            return provider.getBaseItemLevel();
        }
        return 1;
    }

    public static ItemLootCategory getLootCategory(ItemStack stack) {
        if (stack.getItem() instanceof ItemLootProfile profile) {
            return profile.getLootCategory();
        }
        return ItemLootCategory.ONE_HAND_SWORD;
    }

    public static boolean canReceiveAffixes(ItemStack stack) {
        return stack.getItem() instanceof ItemLootProfile profile && profile.canReceiveAffixes();
    }

    public static List<RolledAffix> getPrefixes(ItemStack stack) {
        return readAffixList(stack, TAG_PREFIXES, TAG_PREFIX);
    }

    public static List<RolledAffix> getSuffixes(ItemStack stack) {
        return readAffixList(stack, TAG_SUFFIXES, TAG_SUFFIX);
    }

    public static Optional<RolledAffix> getPrefix(ItemStack stack) {
        List<RolledAffix> prefixes = getPrefixes(stack);
        return prefixes.isEmpty() ? Optional.empty() : Optional.of(prefixes.getFirst());
    }

    public static Optional<RolledAffix> getSuffix(ItemStack stack) {
        List<RolledAffix> suffixes = getSuffixes(stack);
        return suffixes.isEmpty() ? Optional.empty() : Optional.of(suffixes.getFirst());
    }

    public static List<RolledAffix> getAffixes(ItemStack stack) {
        List<RolledAffix> affixes = new ArrayList<>();
        affixes.addAll(getPrefixes(stack));
        affixes.addAll(getSuffixes(stack));
        return affixes;
    }

    public static boolean hasPrefix(ItemStack stack) {
        return !getPrefixes(stack).isEmpty();
    }

    public static boolean hasSuffix(ItemStack stack) {
        return !getSuffixes(stack).isEmpty();
    }

    public static Optional<RareNameRoll> getRareName(ItemStack stack) {
        return readTag(stack).flatMap(tag -> {
            if (!tag.contains(TAG_RARE_NAME, Tag.TAG_STRING)) {
                return Optional.empty();
            }
            String stored = tag.getString(TAG_RARE_NAME);
            if (!stored.contains("/")) {
                return Optional.empty();
            }
            return Optional.of(RareNameRoll.parseStored(stored));
        });
    }

    public static Optional<Long> getInstanceId(ItemStack stack) {
        return readTag(stack).flatMap(tag -> tag.contains(TAG_INSTANCE_ID, Tag.TAG_LONG)
                ? Optional.of(tag.getLong(TAG_INSTANCE_ID))
                : Optional.empty());
    }

    public static boolean isModItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItemHolder().unwrapKey()
                .map(key -> RiftboundMod.MOD_ID.equals(key.location().getNamespace()))
                .orElse(false);
    }

    public static void appendInstanceIdTooltip(ItemStack stack, List<Component> tooltipComponents) {
        if (!usesInstanceId(stack)) {
            return;
        }
        String text = getInstanceId(stack)
                .map(id -> "InstanceId: " + id)
                .orElse("InstanceId: (none)");
        tooltipComponents.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean usesInstanceId(ItemStack stack) {
        return isModItem(stack) && stack.getMaxStackSize() == 1;
    }

    public static boolean isModEquipment(ItemStack stack) {
        return usesInstanceId(stack);
    }

    public static boolean ensureLootDefaults(ItemStack stack) {
        if (!stack.is(ModItems.SHARD_BLADE.get())) {
            return false;
        }

        CompoundTag tag = readTag(stack).orElseGet(CompoundTag::new);
        boolean changed = false;

        if (!tag.contains(TAG_ILVL, Tag.TAG_INT) || tag.getInt(TAG_ILVL) <= 0) {
            tag.putInt(TAG_ILVL, getBaseItemLevel(stack));
            changed = true;
        }
        if (!tag.contains(TAG_RARITY, Tag.TAG_STRING)) {
            tag.putString(TAG_RARITY, ItemRarity.NORMAL.getId());
            changed = true;
        }
        if (!tag.contains(TAG_INSTANCE_ID, Tag.TAG_LONG)) {
            tag.putLong(TAG_INSTANCE_ID, RandomSource.createNewThreadLocalInstance().nextLong());
            changed = true;
        }

        ItemRarity rarity = ItemRarity.fromId(tag.getString(TAG_RARITY));
        ItemRarity normalized = AffixLimits.normalizeRarity(rarity, getPrefixes(stack), getSuffixes(stack));
        if (normalized != rarity) {
            tag.putString(TAG_RARITY, normalized.getId());
            tag.remove(TAG_RARE_NAME);
            changed = true;
        } else if (normalized == ItemRarity.RARE) {
            String storedRareName = tag.contains(TAG_RARE_NAME, Tag.TAG_STRING) ? tag.getString(TAG_RARE_NAME) : "";
            if (!storedRareName.contains("/")) {
                long seed = tag.getLong(TAG_INSTANCE_ID);
                tag.putString(TAG_RARE_NAME, RareNameGenerator.rollSwordName(RandomSource.create(seed)).store());
                changed = true;
            }
        }

        if (changed) {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return changed;
    }

    public static boolean ensureInstanceId(ItemStack stack) {
        if (stack.is(ModItems.SHARD_BLADE.get())) {
            return ensureLootDefaults(stack);
        }
        if (!usesInstanceId(stack) || getInstanceId(stack).isPresent()) {
            return false;
        }
        assignInstanceId(stack);
        return true;
    }

    public static long assignInstanceId(ItemStack stack) {
        long instanceId = RandomSource.createNewThreadLocalInstance().nextLong();
        writeInstanceId(stack, instanceId);
        return instanceId;
    }

    public static boolean deduplicateInstanceId(Container inventory, ItemStack stack, int slotId) {
        if (!usesInstanceId(stack)) {
            return false;
        }

        Optional<Long> instanceId = getInstanceId(stack);
        if (instanceId.isEmpty()) {
            return false;
        }

        long currentId = instanceId.get();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (i == slotId) {
                continue;
            }

            ItemStack other = inventory.getItem(i);
            if (!other.is(stack.getItem())) {
                continue;
            }

            if (getInstanceId(other).orElse(0L) == currentId) {
                assignInstanceId(stack);
                return true;
            }
        }

        return false;
    }

    public static void write(
            ItemStack stack,
            ItemRarity rarity,
            int itemLevel,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes
    ) {
        long instanceId = getInstanceId(stack).orElseGet(() -> RandomSource.createNewThreadLocalInstance().nextLong());
        write(stack, rarity, itemLevel, prefixes, suffixes, instanceId, Optional.empty());
    }

    public static void write(
            ItemStack stack,
            ItemRarity rarity,
            int itemLevel,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes,
            long instanceId
    ) {
        write(stack, rarity, itemLevel, prefixes, suffixes, instanceId, Optional.empty());
    }

    public static void write(
            ItemStack stack,
            ItemRarity rarity,
            int itemLevel,
            List<RolledAffix> prefixes,
            List<RolledAffix> suffixes,
            long instanceId,
            Optional<RareNameRoll> rareName
    ) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_RARITY, rarity.getId());
        tag.putInt(TAG_ILVL, itemLevel);
        tag.putLong(TAG_INSTANCE_ID, instanceId);
        tag.put(TAG_PREFIXES, writeAffixList(prefixes));
        tag.put(TAG_SUFFIXES, writeAffixList(suffixes));
        if (rarity == ItemRarity.RARE) {
            rareName.ifPresent(name -> tag.putString(TAG_RARE_NAME, name.store()));
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void writeInstanceId(ItemStack stack, long instanceId) {
        CompoundTag tag = readTag(stack).orElseGet(CompoundTag::new);
        tag.putLong(TAG_INSTANCE_ID, instanceId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static List<RolledAffix> readAffixList(ItemStack stack, String listKey, String legacyKey) {
        return readTag(stack).map(tag -> {
            if (tag.contains(listKey, Tag.TAG_LIST)) {
                return parseAffixList(tag.getList(listKey, Tag.TAG_COMPOUND));
            }
            if (tag.contains(legacyKey, Tag.TAG_COMPOUND)) {
                return List.of(parseAffixTag(tag.getCompound(legacyKey)));
            }
            if (TAG_PREFIXES.equals(listKey) && tag.contains(TAG_AFFIXES, Tag.TAG_LIST)) {
                ListTag legacy = tag.getList(TAG_AFFIXES, Tag.TAG_COMPOUND);
                if (!legacy.isEmpty()) {
                    return List.of(parseLegacyAffixTag((CompoundTag) legacy.get(0)));
                }
            }
            return Collections.<RolledAffix>emptyList();
        }).orElseGet(Collections::emptyList);
    }

    private static List<RolledAffix> parseAffixList(ListTag listTag) {
        List<RolledAffix> affixes = new ArrayList<>(listTag.size());
        for (Tag entry : listTag) {
            affixes.add(parseAffixTag((CompoundTag) entry));
        }
        return affixes;
    }

    private static ListTag writeAffixList(List<RolledAffix> affixes) {
        ListTag listTag = new ListTag();
        for (RolledAffix affix : affixes) {
            listTag.add(writeAffixTag(affix));
        }
        return listTag;
    }

    private static CompoundTag writeAffixTag(RolledAffix affix) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_AFFIX_ID, affix.id());
        ListTag values = new ListTag();
        for (double value : affix.values()) {
            values.add(DoubleTag.valueOf(value));
        }
        tag.put(TAG_AFFIX_VALUES, values);
        return tag;
    }

    private static RolledAffix parseAffixTag(CompoundTag tag) {
        String id = tag.getString(TAG_AFFIX_ID);
        if (tag.contains(TAG_AFFIX_VALUES, Tag.TAG_LIST)) {
            ListTag valuesTag = tag.getList(TAG_AFFIX_VALUES, Tag.TAG_DOUBLE);
            double[] values = new double[valuesTag.size()];
            for (int i = 0; i < valuesTag.size(); i++) {
                values[i] = valuesTag.getDouble(i);
            }
            return new RolledAffix(id, values);
        }
        return new RolledAffix(id, tag.getDouble(TAG_AFFIX_VALUE));
    }

    private static RolledAffix parseLegacyAffixTag(CompoundTag tag) {
        return new RolledAffix(tag.getString(TAG_AFFIX_ID), tag.getDouble(TAG_AFFIX_VALUE));
    }

    private static Optional<CompoundTag> readTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return Optional.empty();
        }
        CompoundTag tag = customData.copyTag();
        if (tag.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tag);
    }
}
