package com.riftbound.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class OrbOfResonantItem extends Item {
    public static final int MAX_STACK_SIZE = 40;

    public OrbOfResonantItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable("tooltip.riftbound.orb_of_resonant"));
        super.appendHoverText(stack, context, tooltipComponents, flag);
    }
}
