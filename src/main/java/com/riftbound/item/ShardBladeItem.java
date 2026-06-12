package com.riftbound.item;

import com.riftbound.loot.AffixDefinition;
import com.riftbound.loot.ItemRarity;
import com.riftbound.loot.LootDataHelper;
import com.riftbound.loot.RolledAffix;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ShardBladeItem extends SwordItem implements ItemBaseLevelProvider {
    public static final int BASE_ITEM_LEVEL = 1;

    public ShardBladeItem(Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public int getBaseItemLevel() {
        return BASE_ITEM_LEVEL;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            LootDataHelper.ensureInstanceId(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipComponents, flag);

        ItemRarity rarity = LootDataHelper.getRarity(stack);
        tooltipComponents.add(Component.translatable("tooltip.riftbound.rarity." + rarity.getId()).withStyle(rarity.style()));

        int itemLevel = LootDataHelper.getItemLevel(stack);
        if (itemLevel > 1) {
            tooltipComponents.add(Component.translatable("tooltip.riftbound.ilvl", itemLevel));
        }

        for (RolledAffix affix : LootDataHelper.getAffixes(stack)) {
            AffixDefinition definition = resolveAffix(affix.id());
            if (definition != null) {
                tooltipComponents.add(Component.translatable(definition.translationKey() + ".desc", formatValue(definition, affix.value())));
            }
        }

        LootDataHelper.appendInstanceIdTooltip(stack, tooltipComponents);
    }

    private static AffixDefinition resolveAffix(String id) {
        return switch (id) {
            case "sharp" -> AffixDefinition.SHARP;
            case "swift" -> AffixDefinition.SWIFT;
            case "ember" -> AffixDefinition.EMBER;
            case "serrated" -> AffixDefinition.SERRATED;
            case "brutal" -> AffixDefinition.BRUTAL;
            default -> null;
        };
    }

    private static String formatValue(AffixDefinition definition, double value) {
        return switch (definition.type()) {
            case DAMAGE -> String.format("%.1f", value);
            case ATTACK_SPEED -> String.format("%.2f", value);
            case ENCHANT -> "";
        };
    }
}
