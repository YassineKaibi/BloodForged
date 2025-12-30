package com.bloodforged.datagen;

import com.bloodforged.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;

public class ModItemModelProvider extends FabricModelProvider {

    public ModItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Block models later
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        // Register all item models - these will generate JSON files

        // Ores & materials
        itemModelGenerator.register(ModItems.RAW_COPPER, Models.GENERATED);
        itemModelGenerator.register(ModItems.COPPER_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COPPER_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_TIN, Models.GENERATED);
        itemModelGenerator.register(ModItems.TIN_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.TIN_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.BRONZE_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.IRON_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.STEEL_DUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.CARBON, Models.GENERATED);
        itemModelGenerator.register(ModItems.FLUX, Models.GENERATED);

        // Tool parts
        itemModelGenerator.register(ModItems.TOOL_BLADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.TOOL_HANDLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.TOOL_GUARD, Models.GENERATED);
    }
}