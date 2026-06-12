package com.riftbound.menu;

import com.riftbound.registry.ModMenus;
import com.riftbound.transmutation.TransmutationLogic;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransmutationMenu extends AbstractContainerMenu {
    private final Container inputContainer;
    private final Player player;

    public TransmutationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2));
    }

    public TransmutationMenu(int containerId, Inventory playerInventory, Container inputContainer) {
        super(ModMenus.TRANSMUTATION.get(), containerId);
        this.inputContainer = inputContainer;
        this.player = playerInventory.player;

        checkContainerSize(inputContainer, 2);

        addSlot(new Slot(inputContainer, 0, 27, 35));
        addSlot(new Slot(inputContainer, 1, 45, 35));
        addSlot(new TransmutationResultSlot(this, 79, 35));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        broadcastChanges();
    }

    public ItemStack getResultPreview() {
        return TransmutationLogic.getResult(
                inputContainer.getItem(0),
                inputContainer.getItem(1),
                player.registryAccess()
        );
    }

    public void craftResult(Player craftingPlayer) {
        if (getResultPreview().isEmpty()) {
            return;
        }

        if (!craftingPlayer.getAbilities().instabuild) {
            inputContainer.getItem(0).shrink(1);
            inputContainer.getItem(1).shrink(1);
        }

        inputContainer.setChanged();
    }

    @Override
    public ItemStack quickMoveStack(Player movingPlayer, int slotIndex) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            resultStack = stackInSlot.copy();

            if (slotIndex == 2) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, resultStack);
            } else if (slotIndex < 3) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == resultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(movingPlayer, stackInSlot);
        }

        return resultStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, inputContainer);
    }

    private static class TransmutationResultSlot extends Slot {
        private final TransmutationMenu menu;

        TransmutationResultSlot(TransmutationMenu menu, int x, int y) {
            super(new SimpleContainer(1), 0, x, y);
            this.menu = menu;
        }

        @Override
        public ItemStack getItem() {
            return menu.getResultPreview();
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack result = getItem();
            if (result.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack taken = result.copy();
            menu.craftResult(menu.player);
            return taken;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !getItem().isEmpty();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void set(ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }
    }
}
