package com.bloodforged.block;

import com.bloodforged.block.entity.AnvilBlockEntity;
import com.bloodforged.block.entity.ForgeBlockEntity;
import com.bloodforged.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Updated ForgeBlock - Now with BlockEntity support!
 *
 * SIMPLE INTERACTION (Phase 3):
 * - Right-click with item → Insert into forge
 * - Right-click empty hand → Remove from forge
 * - Forge auto-lights when item inserted
 *
 * FUTURE (Phase 5):
 * - GUI for fuel management
 * - Multiple item slots
 * - Temperature control
 */
public class ForgeBlock extends Block implements BlockEntityProvider {

    public ForgeBlock(Settings settings) {
        super(settings);
    }

    // === BLOCK ENTITY PROVIDER ===

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }

        if (type == ModBlockEntities.FORGE_BLOCK_ENTITY) {
            return (BlockEntityTicker<T>) (BlockEntityTicker<ForgeBlockEntity>) ForgeBlockEntity::tick;
        }

        return null;
    }

    // === INTERACTIONS ===

    /**
     * Handle right-click on forge.
     *
     * With item in hand: Insert into forge
     * With empty hand: Extract from forge
     */
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ForgeBlockEntity forgeEntity)) {
            return ActionResult.PASS;
        }

        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack forgeStack = forgeEntity.getStack(ForgeBlockEntity.ITEM_SLOT);

        // If holding item and forge is empty, insert
        if (!handStack.isEmpty() && forgeStack.isEmpty()) {
            // Take one item from hand
            ItemStack toInsert = handStack.split(1);
            forgeEntity.setStack(ForgeBlockEntity.ITEM_SLOT, toInsert);

            // Auto-light forge
            forgeEntity.light();

            return ActionResult.SUCCESS;
        }

        // If empty hand and forge has item, extract
        if (handStack.isEmpty() && !forgeStack.isEmpty()) {
            // Give item to player
            player.setStackInHand(Hand.MAIN_HAND, forgeStack.copy());
            forgeEntity.removeStack(ForgeBlockEntity.ITEM_SLOT);

            // Extinguish forge
            forgeEntity.extinguish();

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    /**
     * Drop items when broken.
     */
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof ForgeBlockEntity forgeEntity) {
            ItemScatterer.spawn(world, pos, forgeEntity);
            world.updateComparators(pos, this);
        }

        return super.onBreak(world, pos, state, player);
    }
}