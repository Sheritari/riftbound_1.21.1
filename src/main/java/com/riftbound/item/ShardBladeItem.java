package com.riftbound.item;

import com.riftbound.loot.AffixTooltipHelper;
import com.riftbound.loot.ItemLootCategory;
import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.LootItemFactory;
import com.riftbound.loot.RolledAffix;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShardBladeItem extends Item implements ItemLootProfile {
    public static final int BASE_ITEM_LEVEL = 1;

    public ShardBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getBaseItemLevel() {
        return BASE_ITEM_LEVEL;
    }

    @Override
    public ItemLootCategory getLootCategory() {
        return ItemLootCategory.ONE_HAND_SWORD;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        boolean changed = LootDataHelper.ensureLootDefaults(stack);
        if (changed) {
            LootItemFactory.refreshDisplayName(stack);
        }
        LootDataHelper.deduplicateInstanceId(player.getInventory(), stack, slotId);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(
                "tooltip.riftbound.attack_damage",
                String.format("%.0f", BladeCombatStats.MIN_BASE_DAMAGE),
                String.format("%.0f", BladeCombatStats.MAX_BASE_DAMAGE)
        ));
        tooltipComponents.add(Component.translatable(
                "tooltip.riftbound.attack_speed",
                String.format("%.2f", BladeCombatStats.getAttackSpeed(stack))
        ));
        tooltipComponents.add(Component.translatable(
                "tooltip.riftbound.reach",
                String.format("%.1f", BladeCombatStats.REACH_BLOCKS)
        ));
        tooltipComponents.add(Component.translatable("tooltip.riftbound.implicit.accuracy"));

        for (RolledAffix affix : LootDataHelper.getPrefixes(stack)) {
            tooltipComponents.add(AffixTooltipHelper.describe(affix));
        }
        for (RolledAffix affix : LootDataHelper.getSuffixes(stack)) {
            tooltipComponents.add(AffixTooltipHelper.describe(affix));
        }

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        tooltipComponents.add(Component.translatable("tooltip.riftbound.rarity." + rarity.getId()).withStyle(rarity.style()));

        int itemLevel = LootDataHelper.getItemLevel(stack);
        if (itemLevel > 1) {
            tooltipComponents.add(Component.translatable("tooltip.riftbound.ilvl", itemLevel));
        }

        super.appendHoverText(stack, context, tooltipComponents, flag);
        LootDataHelper.appendInstanceIdTooltip(stack, tooltipComponents);
    }
}
