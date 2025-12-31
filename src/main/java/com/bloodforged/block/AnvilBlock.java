package com.bloodforged.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AnvilBlock extends Block {

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
}