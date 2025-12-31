package com.bloodforged;

import com.bloodforged.block.ModBlocks;
import com.bloodforged.block.entity.ModBlockEntities;
import com.bloodforged.component.ModDataComponents;
import com.bloodforged.item.ModItems;
import com.bloodforged.material.ExampleMaterials;
import com.bloodforged.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BloodForged main class.
 *
 * INITIALIZATION ORDER MATTERS!
 *
 * 1. Data Components - Must exist before items use them
 * 2. Materials - Foundation for all items
 * 3. Items - Use components and materials
 * 4. Blocks - Use items
 * 5. Block Entities - Extend blocks with logic
 * 6. Screen Handlers - Use block entities
 */
public class BloodForged implements ModInitializer {
    public static final String MOD_ID = "bloodforged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("=== Initializing BloodForged ===");

        // Phase 1: Foundation
        LOGGER.info("[Phase 1] Registering data components...");
        ModDataComponents.registerDataComponents();

        LOGGER.info("[Phase 1] Registering materials...");
        ExampleMaterials.register();

        // Phase 2: Items & Blocks
        LOGGER.info("[Phase 2] Registering items...");
        ModItems.registerModItems();

        LOGGER.info("[Phase 2] Registering blocks...");
        ModBlocks.registerModBlocks();

        // Phase 3: Interactive Systems
        LOGGER.info("[Phase 3] Registering block entities...");
        ModBlockEntities.registerBlockEntities();

        LOGGER.info("[Phase 3] Registering screen handlers...");
        ModScreenHandlers.registerScreenHandlers();

        LOGGER.info("=== BloodForged initialization complete! ===");
    }
}