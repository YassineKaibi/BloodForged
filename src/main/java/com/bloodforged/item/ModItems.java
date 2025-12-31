package com.bloodforged.item;

import com.bloodforged.BloodForged;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    // === ORES & MATERIALS ===

    public static final Item RAW_COPPER = register("raw_copper");
    public static final Item COPPER_INGOT = register("copper_ingot");
    public static final Item COPPER_DUST = register("copper_dust");

    public static final Item RAW_TIN = register("raw_tin");
    public static final Item TIN_INGOT = register("tin_ingot");
    public static final Item TIN_DUST = register("tin_dust");

    public static final Item BRONZE_INGOT = register("bronze_ingot");
    public static final Item BRONZE_DUST = register("bronze_dust");

    public static final Item IRON_DUST = register("iron_dust");

    public static final Item STEEL_INGOT = register("steel_ingot");
    public static final Item STEEL_DUST = register("steel_dust");

    public static final Item CARBON = register("carbon");
    public static final Item FLUX = register("flux");

    // === TOOL PARTS ===

    public static final Item TOOL_BLADE = register("tool_blade",
            new ToolPartItem(new Item.Settings().registryKey(keyOf("tool_blade")), "blade"));

    public static final Item TOOL_HANDLE = register("tool_handle",
            new ToolPartItem(new Item.Settings().registryKey(keyOf("tool_handle")), "handle"));

    public static final Item TOOL_GUARD = register("tool_guard",
            new ToolPartItem(new Item.Settings().registryKey(keyOf("tool_guard")), "guard"));


    // === HELPER METHODS ===

    private static RegistryKey<Item> keyOf(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BloodForged.MOD_ID, name));
    }

    private static Item register(String name) {
        return register(name, new Item(new Item.Settings().registryKey(keyOf(name))));
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BloodForged.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BloodForged.LOGGER.info("Registering items for " + BloodForged.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
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

            entries.add(TOOL_BLADE);
            entries.add(TOOL_HANDLE);
            entries.add(TOOL_GUARD);
        });
    }
}