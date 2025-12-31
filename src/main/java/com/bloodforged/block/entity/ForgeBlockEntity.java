package com.bloodforged.block.entity;

import com.bloodforged.component.ModDataComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * ForgeBlockEntity - Heats metal to working temperature.
 * 
 * SIMPLER than AnvilBlockEntity:
 * - No GUI (for now, Phase 3 simplified)
 * - Just heats items over time
 * - Right-click to insert/remove items
 * 
 * INVENTORY SLOTS:
 * [0] ITEM - Metal to be heated
 * [1] FUEL - Coal, charcoal, etc. (future feature)
 * 
 * HEATING MECHANICS:
 * - Item heats at constant rate
 * - Stops at max temperature (1400°C)
 * - Fuel is consumed (future)
 * - Visual: Block glows when active
 */
public class ForgeBlockEntity extends BlockEntity implements ImplementedInventory {

    // === INVENTORY ===
    
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    
    // === STATE ===
    
    /**
     * Current temperature of item being heated (Celsius).
     */
    private int temperature = 0;
    
    /**
     * Whether forge is currently lit/active.
     */
    private boolean isLit = false;
    
    // === CONSTANTS ===
    
    /**
     * Maximum temperature forge can reach.
     * 
     * Different forge types will have different max temps:
     * - Basic forge: 1200°C
     * - Blast furnace: 1600°C (future)
     * - Ancient forge: 2000°C (future)
     */
    private static final int MAX_TEMPERATURE = 1400;
    
    /**
     * Heating rate (degrees per second).
     * 
     * Real forges heat slowly - takes time to reach temperature.
     * This creates interesting gameplay: plan ahead!
     */
    private static final float HEATING_RATE = 20.0f; // 20°C per second
    
    /**
     * Slot indices.
     */
    public static final int ITEM_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    
    // === CONSTRUCTOR ===
    
    public ForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FORGE_BLOCK_ENTITY, pos, state);
    }
    
    // === INVENTORY INTERFACE ===
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
    
    // === NBT SERIALIZATION ===
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("temperature", temperature);
        nbt.putBoolean("isLit", isLit);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);

        temperature = nbt.getInt("temperature").orElse(0);
        isLit = nbt.getBoolean("isLit").orElse(false);
    }
    
    // === TICKING LOGIC ===
    
    /**
     * Tick - heat item over time.
     */
    public static void tick(World world, BlockPos pos, BlockState state, ForgeBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        ItemStack itemStack = blockEntity.inventory.get(ITEM_SLOT);

        // If has item and is lit, heat it
        if (!itemStack.isEmpty() && blockEntity.isLit) {
            // Heat up - increase by 1 degree per tick (20°C per second)
            blockEntity.temperature += 1;

            // Cap at max
            if (blockEntity.temperature > MAX_TEMPERATURE) {
                blockEntity.temperature = MAX_TEMPERATURE;
            }

            // Apply temperature to item
            itemStack.set(ModDataComponents.TEMPERATURE, blockEntity.temperature);

            blockEntity.markDirty();
        }
        
        // If no item, reset
        if (itemStack.isEmpty()) {
            blockEntity.temperature = 0;
            blockEntity.isLit = false;
        }
    }
    
    // === PUBLIC METHODS ===
    
    /**
     * Light the forge (or it could auto-light when item inserted).
     */
    public void light() {
        this.isLit = true;
        markDirty();
    }
    
    /**
     * Extinguish the forge.
     */
    public void extinguish() {
        this.isLit = false;
        markDirty();
    }
    
    /**
     * Get current temperature.
     */
    public int getTemperature() {
        return temperature;
    }
    
    /**
     * Check if lit.
     */
    public boolean isLit() {
        return isLit;
    }
}
