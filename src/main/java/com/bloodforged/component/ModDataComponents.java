package com.bloodforged.component;

import com.bloodforged.BloodForged;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**Custom data components for items.*/
public class ModDataComponents {

    /**
     * MATERIAL component - stores which material a part is made from
     *
     * Example: "bloodforged:bronze", "bloodforged:iron", "bloodforged:steel"
     *
     * Used on tool parts (blades, handles, guards) to track what they're made of.
     * When assembling a tool, we read this to calculate final stats.
     */
    public static final ComponentType<String> MATERIAL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BloodForged.MOD_ID, "material"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)  // How to serialize/deserialize
                    .build()
    );

    /**
     * QUALITY component - stores forging quality percentage (0-120)
     *
     * This is the CORE of our smithing system!
     *
     * How quality works:
     * - Base quality: 50% (before smithing)
     * - Perfect smithing: up to 120%
     * - Poor smithing: down to 0%
     *
     * Quality multiplies base material stats:
     *   finalDurability = baseDurability * (quality / 100)
     *   finalDamage = baseDamage * (quality / 100)
     *
     * Example:
     *   Bronze blade base: 250 durability, 2.5 damage
     *   With 85% quality: 212 durability, 2.1 damage
     *   With 120% quality: 300 durability, 3.0 damage (master smith!)
     */
    public static final ComponentType<Integer> QUALITY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BloodForged.MOD_ID, "quality"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .build()
    );

    /**
     * TEMPERATURE component - stores temperature in Celsius (0-2000)
     *
     * This enables realistic forging!
     *
     * Temperature workflow:
     * 1. Put metal in forge → heats to 1200°C
     * 2. Take out hot metal → temperature stored on item
     * 3. Put on anvil → temperature starts decreasing
     * 4. Work while hot → mini-game active
     * 5. Too cold (<800°C) → must reheat
     *
     * Temperature thresholds:
     * - < 800°C: Too cold to work (dark gray)
     * - 800-1200°C: Workable (orange glow)
     * - 1200-1400°C: Ideal (yellow-white glow)
     * - > 1400°C: Very hot (white, sparking)
     *
     * Why track on item, not just block entity?
     * - Items can be moved between blocks while staying hot
     * - Realistic: real metal stays hot even when moved
     * - Gameplay: lets player plan their workflow
     */
    public static final ComponentType<Integer> TEMPERATURE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BloodForged.MOD_ID, "temperature"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .build()
    );

    /**
     * Register all components.
     * Called during mod initialization.
     */
    public static void registerDataComponents() {
        BloodForged.LOGGER.info("Registering data components for " + BloodForged.MOD_ID);
    }
}