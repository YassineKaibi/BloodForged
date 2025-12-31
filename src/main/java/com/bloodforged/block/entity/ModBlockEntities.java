package com.bloodforged.block.entity;

import com.bloodforged.BloodForged;
import com.bloodforged.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registry for all Block Entities in BloodForged.
 *
 * WHAT IS A BLOCK ENTITY TYPE?
 * It's like a "recipe" telling Minecraft:
 * - "This block entity class goes with this block"
 * - "When you load this block, create this entity"
 *
 * Without registration:
 *   Block exists, but has no "brain"
 *
 * With registration:
 *   Block + BlockEntity = Functional machine
 */
public class ModBlockEntities {

    /**
     * Anvil Block Entity Type.
     *
     * Links AnvilBlockEntity to AnvilBlock.
     */
    public static final BlockEntityType<AnvilBlockEntity> ANVIL_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(BloodForged.MOD_ID, "smithing_anvil"),
                    FabricBlockEntityTypeBuilder.create(
                            AnvilBlockEntity::new,
                            ModBlocks.SMITHING_ANVIL
                    ).build()
            );

    /**
     * Forge Block Entity Type.
     *
     * Links ForgeBlockEntity to ForgeBlock.
     */
    public static final BlockEntityType<ForgeBlockEntity> FORGE_BLOCK_ENTITY =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(BloodForged.MOD_ID, "forge"),
                    FabricBlockEntityTypeBuilder.create(
                            ForgeBlockEntity::new,
                            ModBlocks.FORGE
                    ).build()
            );

    /**
     * Register all block entities.
     * Called during mod initialization.
     *
     * NOTE: The actual registration happens in the static initializers above.
     * This method just ensures the class is loaded.
     */
    public static void registerBlockEntities() {
        BloodForged.LOGGER.info("Registering block entities for " + BloodForged.MOD_ID);
    }
}