package com.bloodforged;

import com.bloodforged.datagen.ModItemModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BloodForgedDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        // We'll add providers here as we create them
        // pack.addProvider(ItemModelProvider::new);
        // pack.addProvider(RecipeProvider::new);
        // etc.
        pack.addProvider(ModItemModelProvider::new);
    }
}