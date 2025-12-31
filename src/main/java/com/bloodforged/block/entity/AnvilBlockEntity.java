package com.bloodforged.block.entity;

import com.bloodforged.component.ModDataComponents;
import com.bloodforged.screen.AnvilScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * AnvilBlockEntity - The "brain" of the smithing anvil.
 * 
 * RESPONSIBILITIES:
 * 1. Store items (input metal, hammer, output)
 * 2. Track smithing state (temperature, progress, quality)
 * 3. Run mini-game logic
 * 4. Cool down metal over time
 * 5. Create GUI for player
 * 
 * ARCHITECTURE:
 * - Implements ImplementedInventory → Can store items
 * - Implements NamedScreenHandlerFactory → Can create GUIs
 * - Has tick() method → Runs every game tick (20 times per second)
 * - Saves/loads data via NBT
 * 
 * INVENTORY SLOTS:
 * [0] INPUT  - Hot metal to be worked
 * [1] HAMMER - Tool used for smithing (future: different hammers)
 * [2] OUTPUT - Finished tool part
 */
public class AnvilBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {

    // === INVENTORY ===
    
    /**
     * The inventory storage.
     * DefaultedList automatically handles null -> ItemStack.EMPTY conversion.
     * 
     * Slot layout:
     * [0] = Input (hot metal)
     * [1] = Hammer
     * [2] = Output (finished part)
     */
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    
    // === SMITHING STATE ===
    
    /**
     * Current temperature of the metal being worked (in Celsius).
     * 
     * Temperature mechanics:
     * - Starts at item's temperature when placed
     * - Decreases over time (cooling physics)
     * - If drops below 800°C, mini-game stops
     * - Must reheat in forge
     * 
     * Formula: temp -= coolingRate * deltaTime
     */
    private int temperature = 0;
    
    /**
     * Smithing progress (0-100).
     * 
     * Progress represents how close the part is to being finished.
     * Each successful hammer strike increases progress.
     * When reaches 100, the part is complete.
     * 
     * Progress gain per hit:
     * - Perfect hit: +10 progress
     * - Good hit: +7 progress
     * - Okay hit: +3 progress
     */
    private int progress = 0;
    
    /**
     * Quality percentage (0-120).
     * 
     * Quality is the multiplier applied to base material stats.
     * 
     * Starting quality: 50 (neutral)
     * 
     * Quality changes from:
     * - Perfect hits: +5
     * - Good hits: +3
     * - Okay hits: +1
     * - Misses: -2
     * - Working cold metal: -0.1 per tick
     * 
     * Final stats = baseStats * (quality / 100)
     */
    private int quality = 50;
    
    /**
     * Whether smithing mini-game is currently active.
     * 
     * States:
     * - false: Idle, no smithing happening
     * - true: Mini-game running, player must hit targets
     */
    private boolean isSmithing = false;
    
    // === CONSTANTS ===
    
    /**
     * Minimum temperature required to smith (in Celsius).
     * Below this, metal is too hard to work.
     * 
     * Real-world reference: Steel forging temperature ~800-1200°C
     */
    private static final int MIN_SMITHING_TEMP = 800;
    
    /**
     * How fast metal cools (degrees per second).
     * 
     * Cooling rate depends on:
     * - Material density (heavier = slower)
     * - Ambient temperature
     * - Whether being worked (working cools faster)
     * 
     * This is a base value, will be modified by material properties later.
     */
    private static final float COOLING_RATE = 5.0f; // 5°C per second
    
    /**
     * Slot indices (for readability).
     */
    public static final int INPUT_SLOT = 0;
    public static final int HAMMER_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    
    // === PROPERTY DELEGATE ===
    
    /**
     * PropertyDelegate syncs data from server to client.
     * 
     * WHY NEEDED?
     * - Server has the real data (temperature, progress, quality)
     * - Client needs to display it in GUI
     * - Must stay synchronized
     * 
     * HOW IT WORKS:
     * - Server puts values into delegate
     * - Minecraft automatically sends to client
     * - Client reads values from delegate
     * - Client displays in GUI
     * 
     * Index mapping:
     * [0] = temperature
     * [1] = progress
     * [2] = quality
     * [3] = isSmithing (0=false, 1=true)
     */
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AnvilBlockEntity.this.temperature;
                case 1 -> AnvilBlockEntity.this.progress;
                case 2 -> AnvilBlockEntity.this.quality;
                case 3 -> AnvilBlockEntity.this.isSmithing ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AnvilBlockEntity.this.temperature = value;
                case 1 -> AnvilBlockEntity.this.progress = value;
                case 2 -> AnvilBlockEntity.this.quality = value;
                case 3 -> AnvilBlockEntity.this.isSmithing = value != 0;
            }
        }

        @Override
        public int size() {
            return 4; // We sync 4 values
        }
    };
    
    // === CONSTRUCTOR ===
    
    public AnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANVIL_BLOCK_ENTITY, pos, state);
    }
    
    // === INVENTORY INTERFACE ===
    
    /**
     * Required by ImplementedInventory.
     * Returns our inventory storage.
     */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }
    
    // === SCREEN HANDLER FACTORY ===
    
    /**
     * Creates the GUI when player right-clicks anvil.
     * 
     * This is called on the server.
     * Return value is sent to client to open GUI.
     */
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AnvilScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    
    /**
     * Display name for the GUI.
     * Shows at top of screen.
     */
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.bloodforged.smithing_anvil");
    }
    
    // === NBT SERIALIZATION ===
    
    /**
     * Save data to disk.
     * 
     * Called when:
     * - World is saved
     * - Chunk unloads
     * - Player disconnects
     * 
     * Must save:
     * - Inventory contents
     * - Temperature, progress, quality
     * - Smithing state
     */
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        
        // Save inventory
        Inventories.writeNbt(nbt, inventory, registryLookup);
        
        // Save state
        nbt.putInt("temperature", temperature);
        nbt.putInt("progress", progress);
        nbt.putInt("quality", quality);
        nbt.putBoolean("isSmithing", isSmithing);
    }
    
    /**
     * Load data from disk.
     * 
     * Called when:
     * - World loads
     * - Chunk loads
     * - Player joins
     */
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Load inventory
        Inventories.readNbt(nbt, inventory, registryLookup);

        // Load state - unwrap Optional values
        temperature = nbt.getInt("temperature").orElse(0);
        progress = nbt.getInt("progress").orElse(0);
        quality = nbt.getInt("quality").orElse(50);
        isSmithing = nbt.getBoolean("isSmithing").orElse(false);
    }
    // === TICKING LOGIC ===
    
    /**
     * Called every game tick (20 times per second).
     * 
     * IMPORTANT: This runs on SERVER only!
     * Use markDirty() to tell Minecraft to sync changes to client.
     * 
     * What we do each tick:
     * 1. Check if metal is cooling
     * 2. Decrease temperature
     * 3. Stop smithing if too cold
     * 4. Update quality if working cold metal (penalty)
     */
    public static void tick(World world, BlockPos pos, BlockState state, AnvilBlockEntity blockEntity) {
        // Only run on server
        if (world.isClient) {
            return;
        }
        
        // Get input item
        ItemStack inputStack = blockEntity.inventory.get(INPUT_SLOT);
        
        // If there's an item and it's hot, cool it down
        if (!inputStack.isEmpty() && blockEntity.temperature > 0) {
            // Cool down over time
            // COOLING_RATE is per second, we tick 20 times per second
            // Decrease by 1 degree every 4 ticks (5°C per second / 20 ticks = 0.25 per tick)
            // We'll cool 1 degree every 4 ticks instead of trying to handle fractions
            if (world.getTime() % 4 == 0) {
                blockEntity.temperature -= 1;

                // Don't go below 0
                if (blockEntity.temperature < 0) {
                    blockEntity.temperature = 0;
                }

                // Notify block entity changed
                blockEntity.markDirty();
            }

            // If smithing and temperature drops too low, stop smithing
            if (blockEntity.isSmithing && blockEntity.temperature < MIN_SMITHING_TEMP) {
                blockEntity.isSmithing = false;
                blockEntity.quality -= 1; // Penalty for letting it get too cold
                blockEntity.markDirty();
            }
        }
    }
    
    // === PUBLIC METHODS ===
    
    /**
     * Start the smithing mini-game.
     * 
     * Requirements:
     * - Must have hot metal in input slot
     * - Temperature must be >= 800°C
     * - Must have hammer (future feature)
     * 
     * @return true if started successfully
     */
    public boolean startSmithing() {
        ItemStack inputStack = inventory.get(INPUT_SLOT);
        
        // Check requirements
        if (inputStack.isEmpty()) {
            return false; // No item to smith
        }
        
        if (temperature < MIN_SMITHING_TEMP) {
            return false; // Too cold
        }
        
        // Start smithing!
        isSmithing = true;
        progress = 0;
        quality = 50; // Start at neutral quality
        
        markDirty();
        return true;
    }
    
    /**
     * Handle a hammer strike in the mini-game.
     * 
     * @param accuracy How accurate the hit was (0.0 = miss, 1.0 = perfect)
     */
    public void handleHammerStrike(float accuracy) {
        if (!isSmithing) {
            return;
        }
        
        // Calculate progress gain
        int progressGain = 0;
        int qualityGain = 0;
        
        if (accuracy >= 0.9f) {
            // Perfect hit
            progressGain = 10;
            qualityGain = 5;
        } else if (accuracy >= 0.7f) {
            // Good hit
            progressGain = 7;
            qualityGain = 3;
        } else if (accuracy >= 0.4f) {
            // Okay hit
            progressGain = 3;
            qualityGain = 1;
        } else {
            // Miss
            qualityGain = -2;
        }
        
        // Apply changes
        progress += progressGain;
        quality += qualityGain;
        
        // Clamp values
        progress = Math.min(progress, 100);
        quality = Math.max(0, Math.min(120, quality));
        
        // Check if complete
        if (progress >= 100) {
            completeSmithing();
        }
        
        markDirty();
    }
    
    /**
     * Finish smithing and create the output item.
     */
    private void completeSmithing() {
        ItemStack inputStack = inventory.get(INPUT_SLOT);
        
        if (inputStack.isEmpty()) {
            return;
        }
        
        // Create output item with quality
        ItemStack outputStack = inputStack.copy();
        outputStack.set(ModDataComponents.QUALITY, quality);
        outputStack.remove(ModDataComponents.TEMPERATURE); // No longer hot
        
        // Place in output slot
        inventory.set(OUTPUT_SLOT, outputStack);
        
        // Remove input
        inventory.set(INPUT_SLOT, ItemStack.EMPTY);
        
        // Reset state
        isSmithing = false;
        progress = 0;
        quality = 50;
        temperature = 0;
        
        markDirty();
    }
    
    /**
     * Load temperature from input item when placed.
     */
    public void loadTemperatureFromItem() {
        ItemStack inputStack = inventory.get(INPUT_SLOT);
        
        if (!inputStack.isEmpty()) {
            Integer temp = inputStack.get(ModDataComponents.TEMPERATURE);
            if (temp != null) {
                this.temperature = temp;
                markDirty();
            }
        }
    }
}
