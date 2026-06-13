package com.riftbound.menu;

import com.riftbound.block.entity.CageOfTradeBlockEntity;
import com.riftbound.registry.ModBlocks;
import com.riftbound.registry.ModMenus;
import com.riftbound.trade.CageOfTradeLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CageOfTradeMenu extends AbstractContainerMenu {
    public static final int OUTPUT_SLOT_START = CageOfTradeLayout.INPUT_SLOTS;
    public static final int OUTPUT_PRIMARY_SLOT = OUTPUT_SLOT_START;
    public static final int OUTPUT_SLOT_END = OUTPUT_SLOT_START + 1;
    public static final int FIRST_PLAYER_SLOT = OUTPUT_SLOT_END;
    public static final int LAST_PLAYER_SLOT = FIRST_PLAYER_SLOT + 35;

    private final Container inputContainer;
    private final ContainerLevelAccess access;

    public CageOfTradeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        super(ModMenus.CAGE_OF_TRADE.get(), containerId);
        BlockPos pos = extraData.readBlockPos();
        this.access = ContainerLevelAccess.create(playerInventory.player.level(), pos);
        this.inputContainer = resolveInputContainer(playerInventory, pos);
        initializeSlots(playerInventory);
    }

    public CageOfTradeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, Container inputContainer) {
        super(ModMenus.CAGE_OF_TRADE.get(), containerId);
        this.inputContainer = inputContainer;
        this.access = access;
        initializeSlots(playerInventory);
    }

    private void initializeSlots(Inventory playerInventory) {
        checkContainerSize(inputContainer, CageOfTradeLogic.INPUT_SLOTS);

        for (int row = 0; row < CageOfTradeLayout.INPUT_ROWS; row++) {
            for (int column = 0; column < CageOfTradeLayout.INPUT_COLUMNS; column++) {
                int slotIndex = column + row * CageOfTradeLayout.INPUT_COLUMNS;
                addSlot(new MagicInputSlot(
                        inputContainer,
                        slotIndex,
                        CageOfTradeLayout.inputSlotX(column),
                        CageOfTradeLayout.inputSlotY(row)
                ));
            }
        }

        addSlot(new CageOfTradeOutputSlot(
                this,
                CageOfTradeLayout.outputSlotX(0),
                CageOfTradeLayout.outputSlotY(0)
        ));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        CageOfTradeLayout.PLAYER_INV_X + column * 18,
                        CageOfTradeLayout.PLAYER_INV_Y + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    playerInventory,
                    column,
                    CageOfTradeLayout.PLAYER_INV_X + column * 18,
                    CageOfTradeLayout.HOTBAR_Y
            ));
        }
    }

    private static Container resolveInputContainer(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof CageOfTradeBlockEntity cage) {
            return cage.getInputContainer();
        }
        return new SimpleContainer(CageOfTradeLogic.INPUT_SLOTS);
    }

    public ItemStack getOutputPreview() {
        return CageOfTradeLogic.getOutputPreview(inputContainer);
    }

    public void completeTrade(Player player) {
        ItemStack output = getOutputPreview();
        if (output.isEmpty()) {
            return;
        }

        CageOfTradeLogic.consumeInputs(inputContainer);
        inputContainer.setChanged();
        broadcastChanges();
    }

    private static boolean isOutputSlot(int slotIndex) {
        return slotIndex >= OUTPUT_SLOT_START && slotIndex < OUTPUT_SLOT_END;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        broadcastChanges();
    }

    @Override
    public void clicked(int slotIndex, int dragType, ClickType clickType, Player clickingPlayer) {
        if (slotIndex == OUTPUT_PRIMARY_SLOT && clickType == ClickType.PICKUP) {
            ItemStack output = getOutputPreview();
            if (!output.isEmpty()) {
                ItemStack carried = getCarried();
                if (carried.isEmpty()) {
                    completeTrade(clickingPlayer);
                    setCarried(output.copy());
                    return;
                }
                if (ItemStack.isSameItemSameComponents(carried, output)
                        && carried.getCount() + output.getCount() <= carried.getMaxStackSize()) {
                    completeTrade(clickingPlayer);
                    carried.grow(output.getCount());
                    setCarried(carried);
                    return;
                }
            }
        }

        if (isOutputSlot(slotIndex)) {
            return;
        }

        super.clicked(slotIndex, dragType, clickType, clickingPlayer);
        broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (isOutputSlot(slotIndex)) {
            return ItemStack.EMPTY;
        }

        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        original = stackInSlot.copy();

        if (slotIndex < CageOfTradeLogic.INPUT_SLOTS) {
            if (!moveItemStackTo(stackInSlot, FIRST_PLAYER_SLOT, LAST_PLAYER_SLOT + 1, true)) {
                return ItemStack.EMPTY;
            }
        } else if (!CageOfTradeLogic.canPlaceInInput(stackInSlot)
                || !moveItemStackTo(stackInSlot, 0, CageOfTradeLogic.INPUT_SLOTS, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        broadcastChanges();
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.CAGE_OF_TRADE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, inputContainer));
    }

    private static class MagicInputSlot extends Slot {
        MagicInputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return CageOfTradeLogic.canPlaceInInput(stack);
        }
    }

    private static class CageOfTradeOutputSlot extends Slot {
        private final CageOfTradeMenu menu;

        CageOfTradeOutputSlot(CageOfTradeMenu menu, int x, int y) {
            super(new SimpleContainer(1), 0, x, y);
            this.menu = menu;
        }

        @Override
        public ItemStack getItem() {
            return menu.getOutputPreview();
        }

        @Override
        public boolean hasItem() {
            return !getItem().isEmpty();
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

        @Override
        public ItemStack remove(int amount) {
            return ItemStack.EMPTY;
        }
    }
}
