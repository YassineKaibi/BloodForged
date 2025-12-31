package com.bloodforged.screen;

import com.bloodforged.component.ModDataComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * AnvilScreenHandler - Server-side GUI logic for smithing anvil.
 *
 * ARCHITECTURE:
 * ┌─────────────────────────────────────────┐
 * │ Server: AnvilScreenHandler              │
 * │  - Defines slot positions               │
 * │  - Handles item transfers               │
 * │  - Syncs data to client                 │
 * └─────────────────────────────────────────┘
 *          ↓ (network sync)
 * ┌─────────────────────────────────────────┐
 * │ Client: AnvilScreen                     │
 * │  - Renders GUI background               │
 * │  - Draws progress bars                  │
 * │  - Handles mini-game visuals            │
 * └─────────────────────────────────────────┘
 *
 * SLOT LAYOUT:
 * ┌──────────────────────────┐
 * │  [INPUT]  [HAMMER]       │  ← Anvil slots
 * │                          │
 * │      (Anvil surface)     │  ← Mini-game area
 * │                          │
 * │         [OUTPUT]         │  ← Result slot
 * │                          │
 * │  Player Inventory (27)   │  ← Player main inventory
 * │  Player Hotbar (9)       │  ← Player hotbar
 * └──────────────────────────┘
 */
public class AnvilScreenHandler extends ScreenHandler {

    // === COMPONENTS ===

    /**
     * Reference to anvil's inventory.
     * Contains: [INPUT, HAMMER, OUTPUT]
     */
    private final Inventory inventory;

    /**
     * Property delegate for syncing data to client.
     * Syncs: [temperature, progress, quality, isSmithing]
     */
    private final PropertyDelegate propertyDelegate;

    // === SLOT INDICES ===

    // Anvil slots
    private static final int INPUT_SLOT = 0;
    private static final int HAMMER_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    // Player inventory starts after anvil slots
    private static final int PLAYER_INVENTORY_START = 3;
    private static final int PLAYER_INVENTORY_END = 30; // 27 slots
    private static final int PLAYER_HOTBAR_START = 30;
    private static final int PLAYER_HOTBAR_END = 39; // 9 slots

    // === CONSTRUCTORS ===

    /**
     * Client-side constructor.
     * Called when client receives "open GUI" packet.
     * Creates with dummy inventory.
     */
    public AnvilScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(4));
    }

    /**
     * Server-side constructor.
     * Called by AnvilBlockEntity when GUI is opened.
     * Uses real block entity inventory and property delegate.
     */
    public AnvilScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.ANVIL_SCREEN_HANDLER, syncId);

        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        // Check inventory size
        checkSize(inventory, 3);
        inventory.onOpen(playerInventory.player);

        // Add property delegate for data sync
        this.addProperties(propertyDelegate);

        // === ADD SLOTS ===

        // INPUT SLOT (top left) - only accepts hot metal
        this.addSlot(new Slot(inventory, INPUT_SLOT, 56, 17) {
            @Override
            public boolean canInsert(ItemStack stack) {
                // Must have temperature component (from forge)
                Integer temp = stack.get(ModDataComponents.TEMPERATURE);
                return temp != null && temp >= 800;
            }
        });

        // HAMMER SLOT (top right) - for future hammer items
        this.addSlot(new Slot(inventory, HAMMER_SLOT, 80, 17) {
            @Override
            public boolean canInsert(ItemStack stack) {
                // For now, accept any item (future: only hammers)
                return true;
            }
        });

        // OUTPUT SLOT (bottom center) - only for taking, not inserting
        this.addSlot(new Slot(inventory, OUTPUT_SLOT, 124, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Can't insert into output
            }
        });

        // PLAYER INVENTORY (3 rows of 9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9, // Inventory index
                        8 + col * 18,       // X position
                        84 + row * 18       // Y position
                ));
            }
        }

        // PLAYER HOTBAR (1 row of 9)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,                // Hotbar index (0-8)
                    8 + col * 18,       // X position
                    142                 // Y position
            ));
        }
    }

    // === DATA ACCESS (for client rendering) ===

    /**
     * Get current temperature (for rendering).
     */
    public int getTemperature() {
        return propertyDelegate.get(0);
    }

    /**
     * Get smithing progress (0-100).
     */
    public int getProgress() {
        return propertyDelegate.get(1);
    }

    /**
     * Get quality percentage (0-120).
     */
    public int getQuality() {
        return propertyDelegate.get(2);
    }

    /**
     * Check if mini-game is active.
     */
    public boolean isSmithing() {
        return propertyDelegate.get(3) != 0;
    }

    // === SLOT TRANSFER (shift-click) ===

    /**
     * Handle shift-clicking items.
     *
     * RULES:
     * - Shift-click from player inventory → try to put in anvil
     * - Shift-click from anvil → try to put in player inventory
     */
    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            returnStack = slotStack.copy();

            // Clicked anvil slot → move to player inventory
            if (slotIndex < PLAYER_INVENTORY_START) {
                if (!this.insertItem(slotStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(slotStack, returnStack);
            }
            // Clicked player inventory → move to anvil
            else {
                // Try input slot first
                if (!this.insertItem(slotStack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                    // Try hammer slot
                    if (!this.insertItem(slotStack, HAMMER_SLOT, HAMMER_SLOT + 1, false)) {
                        // Move between main inventory and hotbar
                        if (slotIndex < PLAYER_HOTBAR_START) {
                            if (!this.insertItem(slotStack, PLAYER_HOTBAR_START, PLAYER_HOTBAR_END, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            if (!this.insertItem(slotStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return returnStack;
    }

    // === VALIDATION ===

    /**
     * Check if player can still use this GUI.
     * Prevents using GUI when too far away.
     */
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    /**
     * Clean up when GUI is closed.
     */
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    // === HELPER CLASS ===

    /**
     * Simple inventory implementation for client-side.
     * Server uses real BlockEntity inventory.
     */
    private static class SimpleInventory implements Inventory {
        private final ItemStack[] items;

        public SimpleInventory(int size) {
            this.items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = ItemStack.EMPTY;
            }
        }

        @Override public int size() { return items.length; }
        @Override public boolean isEmpty() { return true; }
        @Override public ItemStack getStack(int slot) { return items[slot]; }
        @Override public ItemStack removeStack(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeStack(int slot) { return ItemStack.EMPTY; }
        @Override public void setStack(int slot, ItemStack stack) { items[slot] = stack; }
        @Override public void markDirty() { }
        @Override public boolean canPlayerUse(PlayerEntity player) { return true; }
        @Override public void clear() { }
    }
}