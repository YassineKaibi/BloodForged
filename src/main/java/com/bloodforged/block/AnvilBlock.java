package com.bloodforged.block;

import com.bloodforged.block.entity.AnvilBlockEntity;
import com.bloodforged.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Updated AnvilBlock - Now with BlockEntity support!
 *
 * CHANGES FROM PHASE 2:
 * - Implements BlockEntityProvider (tells Minecraft this block has an entity)
 * - createBlockEntity() creates the AnvilBlockEntity
 * - getTicker() runs tick logic
 * - onUse() opens GUI when right-clicked
 * - onStateReplaced() drops items when broken
 */
public class AnvilBlock extends Block implements BlockEntityProvider {

    // Anvil shape (smaller than full block)
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 0, 2, 14, 4, 14),  // Base
            Block.createCuboidShape(4, 4, 4, 12, 10, 12), // Middle
            Block.createCuboidShape(0, 10, 3, 16, 16, 13) // Top (working surface)
    );

    public AnvilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    // === BLOCK ENTITY PROVIDER ===

    /**
     * Create the block entity when block is placed.
     * Called automatically by Minecraft.
     */
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilBlockEntity(pos, state);
    }

    /**
     * Get the ticker that runs every game tick.
     * IMPORTANT: Only return ticker on server side!
     * Client doesn't need to run logic, just display.
     */
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Only tick on server
        if (world.isClient) {
            return null;
        }

        // Check if the type matches our anvil block entity
        if (type == ModBlockEntities.ANVIL_BLOCK_ENTITY) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<AnvilBlockEntity>) AnvilBlockEntity::tick;
        }

        return null;
    }

    // === INTERACTIONS ===

    /**
     * Handle right-click on anvil.
     * Opens the smithing GUI.
     */
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            // Get the block entity
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof AnvilBlockEntity anvilEntity) {
                // Load temperature from item if needed
                anvilEntity.loadTemperatureFromItem();

                // Open GUI
                player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
            }
        }

        return ActionResult.SUCCESS;
    }

    /**
     * Handle block being broken.
     * Drop all items in inventory.
     */
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AnvilBlockEntity anvilEntity) {
            // Drop all items
            ItemScatterer.spawn(world, pos, anvilEntity);

            // Update comparators
            world.updateComparators(pos, this);
        }

        return super.onBreak(world, pos, state, player);
    }
}