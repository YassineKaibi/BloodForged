package com.bloodforged.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/**
 * Helper interface that implements common Inventory methods.
 * 
 * WHY THIS EXISTS:
 * Minecraft's Inventory interface requires implementing ~10 methods.
 * Most are boilerplate that's the same for every inventory.
 * This interface provides default implementations.
 * 
 * HOW TO USE:
 * 1. Implement this interface
 * 2. Override getItems() to return your DefaultedList
 * 3. Done! All other methods work automatically.
 * 
 * Example:
 *   public class MyBlockEntity implements ImplementedInventory {
 *       private DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);
 *       
 *       @Override
 *       public DefaultedList<ItemStack> getItems() {
 *           return items;
 *       }
 *   }
 * 
 * This pattern is commonly used in Fabric mods to reduce boilerplate.
 */
public interface ImplementedInventory extends Inventory {

    /**
     * Retrieve the item list.
     * Must be implemented by the class.
     */
    DefaultedList<ItemStack> getItems();

    /**
     * Get item from slot.
     * 
     * @param slot Slot index (0, 1, 2, etc.)
     * @return ItemStack in that slot, or ItemStack.EMPTY if empty
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    /**
     * Remove items from slot.
     * 
     * Called when:
     * - Player takes items
     * - Hopper extracts items
     * - Recipe consumes items
     * 
     * @param slot Slot to remove from
     * @param count How many to remove
     * @return ItemStack that was removed
     */
    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        
        // If something changed, mark dirty
        if (!result.isEmpty()) {
            markDirty();
        }
        
        return result;
    }

    /**
     * Remove entire stack from slot.
     * 
     * @param slot Slot to clear
     * @return ItemStack that was removed
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Put item into slot.
     * 
     * Called when:
     * - Player places item
     * - Hopper inserts item
     * - Recipe outputs item
     * 
     * @param slot Slot to put into
     * @param stack ItemStack to place
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        
        // Stack too big? Shrink to max size
        if (stack.getCount() > getMaxCount(stack)) {
            stack.setCount(getMaxCount(stack));
        }
        
        markDirty();
    }

    /**
     * Get inventory size.
     * 
     * @return Number of slots
     */
    @Override
    default int size() {
        return getItems().size();
    }

    /**
     * Check if inventory is empty.
     * 
     * @return true if all slots are empty
     */
    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clear all items from inventory.
     * Called when block is broken.
     */
    @Override
    default void clear() {
        getItems().clear();
    }

    /**
     * Mark inventory as changed.
     * Must tell Minecraft to:
     * - Save to disk
     * - Sync to client
     * - Update comparators, etc.
     */
    @Override
    default void markDirty() {
        // Block entity will override this
    }

    /**
     * Check if player can use this inventory.
     * 
     * Standard behavior: Player must be within 8 blocks
     * 
     * @param player Player trying to access
     * @return true if allowed
     */
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}
