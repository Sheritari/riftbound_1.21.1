package com.riftbound.menu;

import com.riftbound.loot.LootDataHelper;
import com.riftbound.registry.ModMenus;
import com.riftbound.transmutation.TransmutationLogic;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransmutationMenu extends AbstractContainerMenu {
    private static final int SEED_HIGH_INDEX = 0;
    private static final int SEED_LOW_INDEX = 1;

    private final Container inputContainer;
    private final Player player;
    private final ContainerData craftSeed = new SimpleContainerData(2);

    public TransmutationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2));
    }

    public TransmutationMenu(int containerId, Inventory playerInventory, Container inputContainer) {
        super(ModMenus.TRANSMUTATION.get(), containerId);
        this.inputContainer = inputContainer;
        this.player = playerInventory.player;

        checkContainerSize(inputContainer, 2);

        addSlot(new Slot(inputContainer, 0, TransmutationLayout.INPUT_1_X, TransmutationLayout.INPUT_1_Y));
        addSlot(new Slot(inputContainer, 1, TransmutationLayout.INPUT_2_X, TransmutationLayout.INPUT_2_Y));
        addSlot(new TransmutationResultSlot(this, TransmutationLayout.RESULT_X, TransmutationLayout.RESULT_Y));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        TransmutationLayout.PLAYER_INV_X + column * 18,
                        TransmutationLayout.PLAYER_INV_Y + row * 18
                ));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    playerInventory,
                    column,
                    TransmutationLayout.PLAYER_INV_X + column * 18,
                    TransmutationLayout.HOTBAR_Y
            ));
        }

        addDataSlots(craftSeed);
    }

    @Override
    public void slotsChanged(Container container) {
        if (!player.level().isClientSide()) {
            ensureUniqueBladeIds();
            updateSyncedSeed();
        }
        super.slotsChanged(container);
        broadcastChanges();
    }

    private void ensureUniqueBladeIds() {
        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = inputContainer.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            boolean changed = LootDataHelper.ensureLootDefaults(stack);
            changed |= LootDataHelper.deduplicateInstanceId(inputContainer, stack, slot);
            if (changed) {
                inputContainer.setItem(slot, stack);
            }
        }
    }

    private void updateSyncedSeed() {
        ItemStack first = inputContainer.getItem(0);
        ItemStack second = inputContainer.getItem(1);

        if (!TransmutationLogic.canCombine(first, second)) {
            craftSeed.set(SEED_HIGH_INDEX, 0);
            craftSeed.set(SEED_LOW_INDEX, 0);
            return;
        }

        long seed = TransmutationLogic.combinationSeed(first, second);
        craftSeed.set(SEED_HIGH_INDEX, (int) (seed >> 32));
        craftSeed.set(SEED_LOW_INDEX, (int) seed);
    }

    private long syncedSeed() {
        return ((long) craftSeed.get(SEED_HIGH_INDEX) << 32) | (craftSeed.get(SEED_LOW_INDEX) & 0xFFFFFFFFL);
    }

    public ItemStack getResultPreview() {
        ItemStack first = inputContainer.getItem(0);
        ItemStack second = inputContainer.getItem(1);

        if (!TransmutationLogic.canCombine(first, second)) {
            return ItemStack.EMPTY;
        }

        long seed = syncedSeed();
        if (seed == 0L) {
            return ItemStack.EMPTY;
        }

        return TransmutationLogic.getResultWithSeed(first, second, seed, player.registryAccess());
    }

    public void craftResult(Player craftingPlayer) {
        ItemStack result = getResultPreview();
        if (result.isEmpty()) {
            return;
        }

        TransmutationLogic.consumeInputs(inputContainer);

        inputContainer.setChanged();
        if (!player.level().isClientSide()) {
            ensureUniqueBladeIds();
            updateSyncedSeed();
        }
        broadcastChanges();
    }

    @Override
    public void clicked(int slotIndex, int dragType, ClickType clickType, Player clickingPlayer) {
        if (slotIndex == 2 && clickType == ClickType.PICKUP) {
            ItemStack result = getResultPreview();
            if (!result.isEmpty()) {
                ItemStack carried = getCarried();
                if (carried.isEmpty()) {
                    craftResult(clickingPlayer);
                    setCarried(result.copy());
                    return;
                }
                if (ItemStack.isSameItemSameComponents(carried, result) && carried.getCount() < carried.getMaxStackSize()) {
                    craftResult(clickingPlayer);
                    carried.grow(1);
                    setCarried(carried);
                    return;
                }
            }
        }

        if (slotIndex == 2) {
            return;
        }

        super.clicked(slotIndex, dragType, clickType, clickingPlayer);
        if (!clickingPlayer.level().isClientSide()) {
            ensureUniqueBladeIds();
            updateSyncedSeed();
        }
        broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player movingPlayer, int slotIndex) {
        if (slotIndex == 2) {
            return ItemStack.EMPTY;
        }

        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            resultStack = stackInSlot.copy();

            if (slotIndex < 2) {
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

        if (!movingPlayer.level().isClientSide()) {
            ensureUniqueBladeIds();
            updateSyncedSeed();
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
