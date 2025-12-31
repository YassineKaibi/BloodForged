package com.bloodforged.screen;

import com.bloodforged.BloodForged;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * Registry for Screen Handlers (GUI types).
 *
 * WHAT IS A SCREEN HANDLER TYPE?
 * It's the "recipe" for creating GUIs.
 *
 * Tells Minecraft:
 * - "When player opens this GUI, create this ScreenHandler"
 * - "This handler manages these slots and data"
 *
 * REGISTRATION FLOW:
 * 1. Register ScreenHandlerType here
 * 2. Block opens GUI → creates ScreenHandler using this type
 * 3. Client receives type → renders matching Screen
 */
public class ModScreenHandlers {

    /**
     * Anvil Screen Handler Type.
     *
     * Used when player opens smithing anvil GUI.
     * Creates AnvilScreenHandler with proper sync.
     */
    public static final ScreenHandlerType<AnvilScreenHandler> ANVIL_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(BloodForged.MOD_ID, "smithing_anvil"),
                    new ScreenHandlerType<>(AnvilScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
            );

    /**
     * Register all screen handlers.
     * Called during mod initialization.
     */
    public static void registerScreenHandlers() {
        BloodForged.LOGGER.info("Registering screen handlers for " + BloodForged.MOD_ID);
    }
}