package com.bloodforged.block;

import com.bloodforged.BloodForged;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    // Anvil - for smithing
    public static final Block SMITHING_ANVIL = registerBlock("smithing_anvil",
            new AnvilBlock(AbstractBlock.Settings.create()
                    .registryKey(blockKeyOf("smithing_anvil"))
                    .strength(5.0f, 1200.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.ANVIL)
            )
    );

    // Forge - for heating metal
    public static final Block FORGE = registerBlock("forge",
            new ForgeBlock(AbstractBlock.Settings.create()
                    .registryKey(blockKeyOf("forge"))
                    .strength(3.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 13)
            )
    );

    // Open Furnace - for smelting
    public static final Block OPEN_FURNACE = registerBlock("open_furnace",
            new OpenFurnaceBlock(AbstractBlock.Settings.create()
                    .registryKey(blockKeyOf("open_furnace"))
                    .strength(3.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)
            )
    );


    // === HELPER METHODS ===

    private static RegistryKey<Block> blockKeyOf(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(BloodForged.MOD_ID, name));
    }

    private static RegistryKey<Item> itemKeyOf(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(BloodForged.MOD_ID, name));
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(BloodForged.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(BloodForged.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(itemKeyOf(name))));
    }

    public static void registerModBlocks() {
        BloodForged.LOGGER.info("Registering blocks for " + BloodForged.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(SMITHING_ANVIL);
            entries.add(FORGE);
            entries.add(OPEN_FURNACE);
        });
    }
}