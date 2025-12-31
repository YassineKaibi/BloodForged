package com.bloodforged;

import com.bloodforged.block.ModBlocks;
import com.bloodforged.component.ModDataComponents;
import com.bloodforged.item.ModItems;
import com.bloodforged.material.ExampleMaterials;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloodForged implements ModInitializer {
    public static final String MOD_ID = "bloodforged";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing BloodForged");

        //FIRST - components
        ModDataComponents.registerDataComponents();
        //SECOND - materials
        ExampleMaterials.register();
        //THIRD - items
        ModItems.registerModItems();
        //FOURTH - blocks
        ModBlocks.registerModBlocks(); // Add this line
    }
}