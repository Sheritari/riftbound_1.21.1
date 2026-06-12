package com.riftbound.loot;

import com.riftbound.RiftboundMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
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
    private static final String TAG_AFFIXES = "Affixes";
    private static final String TAG_AFFIX_ID = "Id";
    private static final String TAG_AFFIX_VALUE = "Value";
    private static final String TAG_INSTANCE_ID = "InstanceId";

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
        if (!isModItem(stack)) {
            return;
        }
        String text = getInstanceId(stack)
                .map(id -> "InstanceId: " + id)
                .orElse("InstanceId: (none)");
        tooltipComponents.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean ensureInstanceId(ItemStack stack) {
        if (!isModItem(stack) || getInstanceId(stack).isPresent()) {
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

    public static void write(ItemStack stack, ItemRarity rarity, int itemLevel, List<RolledAffix> affixes) {
        long instanceId = getInstanceId(stack).orElseGet(() -> RandomSource.createNewThreadLocalInstance().nextLong());
        write(stack, rarity, itemLevel, affixes, instanceId);
    }

    public static void write(ItemStack stack, ItemRarity rarity, int itemLevel, List<RolledAffix> affixes, long instanceId) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_RARITY, rarity.getId());
        tag.putInt(TAG_ILVL, itemLevel);
        tag.putLong(TAG_INSTANCE_ID, instanceId);

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

    private static void writeInstanceId(ItemStack stack, long instanceId) {
        CompoundTag tag = readTag(stack).orElseGet(CompoundTag::new);
        tag.putLong(TAG_INSTANCE_ID, instanceId);
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
