package com.bloodforged.component;

import com.bloodforged.BloodForged;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModDataComponents {

    // Stores which material a part is made from (e.g., "bloodforged:bronze")
    public static final ComponentType<String> MATERIAL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BloodForged.MOD_ID, "material"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .build()
    );

    // Stores quality percentage (0-120)
    public static final ComponentType<Integer> QUALITY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BloodForged.MOD_ID, "quality"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .build()
    );

    public static void registerDataComponents() {
        BloodForged.LOGGER.info("Registering data components for " + BloodForged.MOD_ID);
    }
}