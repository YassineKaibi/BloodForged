package com.bloodforged.item;

import com.bloodforged.BloodForged;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // === ORES & MATERIALS ===

    // Copper (early game)
    public static final Item RAW_COPPER = registerItem("raw_copper", new Item(new Item.Settings()));
    public static final Item COPPER_INGOT = registerItem("copper_ingot", new Item(new Item.Settings()));
    public static final Item COPPER_DUST = registerItem("copper_dust", new Item(new Item.Settings()));

    // Tin (for bronze alloy)
    public static final Item RAW_TIN = registerItem("raw_tin", new Item(new Item.Settings()));
    public static final Item TIN_INGOT = registerItem("tin_ingot", new Item(new Item.Settings()));
    public static final Item TIN_DUST = registerItem("tin_dust", new Item(new Item.Settings()));

    // Bronze (alloy of copper + tin)
    public static final Item BRONZE_INGOT = registerItem("bronze_ingot", new Item(new Item.Settings()));
    public static final Item BRONZE_DUST = registerItem("bronze_dust", new Item(new Item.Settings()));

    // Iron (vanilla has iron, but we need dust)
    public static final Item IRON_DUST = registerItem("iron_dust", new Item(new Item.Settings()));

    // Steel (alloy of iron + carbon)
    public static final Item STEEL_INGOT = registerItem("steel_ingot", new Item(new Item.Settings()));
    public static final Item STEEL_DUST = registerItem("steel_dust", new Item(new Item.Settings()));

    // Special materials
    public static final Item CARBON = registerItem("carbon", new Item(new Item.Settings()));
    public static final Item FLUX = registerItem("flux", new Item(new Item.Settings()));


    // === TOOL PARTS (we'll add components later) ===

    // For now, just basic items - we'll add material/quality data in the next step
    public static final Item TOOL_BLADE = registerItem("tool_blade", new Item(new Item.Settings()));
    public static final Item TOOL_HANDLE = registerItem("tool_handle", new Item(new Item.Settings()));
    public static final Item TOOL_GUARD = registerItem("tool_guard", new Item(new Item.Settings()));


    // === HELPER METHODS ===

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BloodForged.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BloodForged.LOGGER.info("Registering items for " + BloodForged.MOD_ID);

        // Add to creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            // Ores & materials
            entries.add(RAW_COPPER);
            entries.add(COPPER_INGOT);
            entries.add(COPPER_DUST);
            entries.add(RAW_TIN);
            entries.add(TIN_INGOT);
            entries.add(TIN_DUST);
            entries.add(BRONZE_INGOT);
            entries.add(BRONZE_DUST);
            entries.add(IRON_DUST);
            entries.add(STEEL_INGOT);
            entries.add(STEEL_DUST);
            entries.add(CARBON);
            entries.add(FLUX);

            // Tool parts
            entries.add(TOOL_BLADE);
            entries.add(TOOL_HANDLE);
            entries.add(TOOL_GUARD);
        });
    }
}