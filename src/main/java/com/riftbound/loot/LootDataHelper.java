package com.riftbound.loot;

import com.riftbound.RiftboundMod;
import com.riftbound.item.ItemBaseLevelProvider;
import com.riftbound.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LootDataHelper {
    private static final String TAG_RARITY = "Rarity";
    private static final String TAG_ILVL = "Ilvl";
    private static final String TAG_PREFIX = "Prefix";
    private static final String TAG_SUFFIX = "Suffix";
    private static final String TAG_AFFIXES = "Affixes";
    private static final String TAG_AFFIX_ID = "Id";
    private static final String TAG_AFFIX_VALUE = "Value";
    private static final String TAG_AFFIX_VALUES = "Values";
    private static final String TAG_INSTANCE_ID = "InstanceId";

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

    public static Optional<RolledAffix> getPrefix(ItemStack stack) {
        return readAffixTag(stack, TAG_PREFIX);
    }

    public static Optional<RolledAffix> getSuffix(ItemStack stack) {
        return readAffixTag(stack, TAG_SUFFIX);
    }

    public static List<RolledAffix> getAffixes(ItemStack stack) {
        List<RolledAffix> affixes = new ArrayList<>();
        getPrefix(stack).ifPresent(affixes::add);
        getSuffix(stack).ifPresent(affixes::add);
        return affixes;
    }

    public static boolean hasPrefix(ItemStack stack) {
        return getPrefix(stack).isPresent();
    }

    public static boolean hasSuffix(ItemStack stack) {
        return getSuffix(stack).isPresent();
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

    public static void write(
            ItemStack stack,
            ItemRarity rarity,
            int itemLevel,
            Optional<RolledAffix> prefix,
            Optional<RolledAffix> suffix
    ) {
        long instanceId = getInstanceId(stack).orElseGet(() -> RandomSource.createNewThreadLocalInstance().nextLong());
        write(stack, rarity, itemLevel, prefix, suffix, instanceId);
    }

    public static void write(
            ItemStack stack,
            ItemRarity rarity,
            int itemLevel,
            Optional<RolledAffix> prefix,
            Optional<RolledAffix> suffix,
            long instanceId
    ) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_RARITY, rarity.getId());
        tag.putInt(TAG_ILVL, itemLevel);
        tag.putLong(TAG_INSTANCE_ID, instanceId);

        prefix.ifPresent(affix -> tag.put(TAG_PREFIX, writeAffixTag(affix)));
        suffix.ifPresent(affix -> tag.put(TAG_SUFFIX, writeAffixTag(affix)));

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void writeInstanceId(ItemStack stack, long instanceId) {
        CompoundTag tag = readTag(stack).orElseGet(CompoundTag::new);
        tag.putLong(TAG_INSTANCE_ID, instanceId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static Optional<RolledAffix> readAffixTag(ItemStack stack, String key) {
        return readTag(stack).flatMap(tag -> {
            if (tag.contains(key, Tag.TAG_COMPOUND)) {
                return Optional.of(parseAffixTag(tag.getCompound(key)));
            }
            if (TAG_PREFIX.equals(key) && tag.contains(TAG_AFFIXES, Tag.TAG_LIST)) {
                ListTag legacy = tag.getList(TAG_AFFIXES, Tag.TAG_COMPOUND);
                if (!legacy.isEmpty()) {
                    return Optional.of(parseLegacyAffixTag((CompoundTag) legacy.get(0)));
                }
            }
            return Optional.empty();
        });
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
