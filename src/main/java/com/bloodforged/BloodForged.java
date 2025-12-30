package com.bloodforged;

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

        ModItems.registerModItems();
        ExampleMaterials.register();
    }
}