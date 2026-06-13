package com.riftbound.item;

import com.riftbound.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ResonantShardItem extends Item {
    public static final int MAX_STACK_SIZE = 20;
    public static final int SHARDS_PER_ORB = 20;

    public ResonantShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide() || stack.getCount() < SHARDS_PER_ORB || !(entity instanceof Player player)) {
            return;
        }

        int orbCount = stack.getCount() / SHARDS_PER_ORB;
        stack.shrink(orbCount * SHARDS_PER_ORB);

        ItemStack orbs = new ItemStack(ModItems.ORB_OF_RESONANT.get(), orbCount);
        if (!player.getInventory().add(orbs)) {
            player.drop(orbs, false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        tooltipComponents.add(Component.translatable("tooltip.riftbound.resonant_shard"));
        super.appendHoverText(stack, context, tooltipComponents, flag);
    }
}
