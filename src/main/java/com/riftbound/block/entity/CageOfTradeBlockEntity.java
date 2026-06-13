package com.riftbound.block.entity;

import com.riftbound.menu.CageOfTradeMenu;
import com.riftbound.registry.ModBlockEntities;
import com.riftbound.trade.CageOfTradeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CageOfTradeBlockEntity extends BlockEntity implements MenuProvider {
    private final SimpleContainer inventory = new SimpleContainer(CageOfTradeLogic.INPUT_SLOTS) {
        @Override
        public void setChanged() {
            super.setChanged();
            CageOfTradeBlockEntity.this.setChanged();
        }
    };

    public CageOfTradeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAGE_OF_TRADE.get(), pos, state);
    }

    public SimpleContainer getInputContainer() {
        return inventory;
    }

    public ContainerLevelAccess createAccess() {
        return ContainerLevelAccess.create(level, worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.riftbound.cage_of_trade");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CageOfTradeMenu(containerId, playerInventory, createAccess(), inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(CageOfTradeLogic.INPUT_SLOTS, ItemStack.EMPTY);
        for (int slot = 0; slot < CageOfTradeLogic.INPUT_SLOTS; slot++) {
            items.set(slot, inventory.getItem(slot));
        }
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> items = NonNullList.withSize(CageOfTradeLogic.INPUT_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        for (int slot = 0; slot < CageOfTradeLogic.INPUT_SLOTS; slot++) {
            inventory.setItem(slot, items.get(slot));
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int slot = 0; slot < CageOfTradeLogic.INPUT_SLOTS; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
    }
}
