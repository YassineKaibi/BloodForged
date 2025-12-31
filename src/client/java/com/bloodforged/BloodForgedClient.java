package com.bloodforged;

import com.bloodforged.screen.AnvilScreen;
import com.bloodforged.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

/**
 * Client-side initialization for BloodForged.
 *
 * IMPORTANT: This ONLY runs on the client!
 * Server doesn't have screens/rendering.
 *
 * RESPONSIBILITIES:
 * - Register screen factories (link ScreenHandler to Screen)
 * - Register model providers (future)
 * - Register custom renderers (future)
 * - Handle client-specific keybinds (future)
 */
public class BloodForgedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BloodForged.LOGGER.info("Initializing BloodForged client");

        // === REGISTER SCREENS ===

        /**
         * Register screen factory for anvil GUI.
         *
         * This tells Minecraft:
         * "When you receive AnvilScreenHandler from server, render it with AnvilScreen"
         *
         * Flow:
         * 1. Server sends: "Open GUI with AnvilScreenHandler"
         * 2. Client receives, looks up screen factory
         * 3. Client creates AnvilScreen to display
         */
        HandledScreens.register(ModScreenHandlers.ANVIL_SCREEN_HANDLER, AnvilScreen::new);
    }
}