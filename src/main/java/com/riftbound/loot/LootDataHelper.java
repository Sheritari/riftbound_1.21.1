package com.riftbound.loot;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

public final class LootDataHelper {
    private static final String TAG_RARITY = "Rarity";
    private static final String TAG_ILVL = "Ilvl";
    private static final String TAG_AFFIXES = "Affixes";
    private static final String TAG_AFFIX_ID = "Id";
    private static final String TAG_AFFIX_VALUE = "Value";

    private LootDataHelper() {
    }

    public static ItemRarity getRarity(ItemStack stack) {
        return readTag(stack).map(tag -> ItemRarity.fromId(tag.getString(TAG_RARITY))).orElse(ItemRarity.NORMAL);
    }

    public static int getItemLevel(ItemStack stack) {
        return readTag(stack).map(tag -> tag.getInt(TAG_ILVL)).orElse(1);
    }

    public static List<RolledAffix> getAffixes(ItemStack stack) {
        List<RolledAffix> affixes = new ArrayList<>();
        readTag(stack).ifPresent(tag -> {
            if (!tag.contains(TAG_AFFIXES, Tag.TAG_LIST)) {
                return;
            }
            ListTag list = tag.getList(TAG_AFFIXES, Tag.TAG_COMPOUND);
            for (Tag entry : list) {
                CompoundTag affixTag = (CompoundTag) entry;
                affixes.add(new RolledAffix(affixTag.getString(TAG_AFFIX_ID), affixTag.getDouble(TAG_AFFIX_VALUE)));
            }
        });
        return affixes;
    }

    public static void write(ItemStack stack, ItemRarity rarity, int itemLevel, List<RolledAffix> affixes) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_RARITY, rarity.getId());
        tag.putInt(TAG_ILVL, itemLevel);

        ListTag affixList = new ListTag();
        for (RolledAffix affix : affixes) {
            CompoundTag affixTag = new CompoundTag();
            affixTag.putString(TAG_AFFIX_ID, affix.id());
            affixTag.putDouble(TAG_AFFIX_VALUE, affix.value());
            affixList.add(affixTag);
        }
        tag.put(TAG_AFFIXES, affixList);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static java.util.Optional<CompoundTag> readTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return java.util.Optional.empty();
        }
        CompoundTag tag = customData.copyTag();
        if (tag.isEmpty()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(tag);
    }
}
