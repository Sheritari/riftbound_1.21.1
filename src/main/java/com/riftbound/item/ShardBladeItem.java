package com.riftbound.item;

import com.riftbound.loot.AffixDefinition;
import com.riftbound.loot.AffixPool;
import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.RolledAffix;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShardBladeItem extends Item implements ItemBaseLevelProvider {
    public static final int BASE_ITEM_LEVEL = 1;

    public ShardBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getBaseItemLevel() {
        return BASE_ITEM_LEVEL;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            LootDataHelper.ensureInstanceId(stack);
            BladeCombatStats.ensureAttributes(stack, level.registryAccess());
        }
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
                String.format("%.2f", BladeCombatStats.ATTACK_SPEED)
        ));
        tooltipComponents.add(Component.translatable(
                "tooltip.riftbound.reach",
                String.format("%.1f", BladeCombatStats.REACH_BLOCKS)
        ));

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        tooltipComponents.add(Component.translatable("tooltip.riftbound.rarity." + rarity.getId()).withStyle(rarity.style()));

        int itemLevel = LootDataHelper.getItemLevel(stack);
        if (itemLevel > 1) {
            tooltipComponents.add(Component.translatable("tooltip.riftbound.ilvl", itemLevel));
        }

        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = AffixPool.byId(affix.id());
            if (definition != null) {
                tooltipComponents.add(Component.translatable(definition.translationKey() + ".desc", formatValue(definition, affix.value())));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, flag);
        LootDataHelper.appendInstanceIdTooltip(stack, tooltipComponents);
    }

    private static String formatValue(AffixDefinition definition, double value) {
        return switch (definition.type()) {
            case DAMAGE -> String.format("%.1f", value);
            case ATTACK_SPEED -> String.format("%.2f", value);
            case ENCHANT -> "";
        };
    }
}
