package com.bloodforged.material;

import net.minecraft.util.Identifier;
import java.util.Objects;

/**
 * Represents a material that can be used to craft tool parts.
 * 
 * CONCEPT:
 * Materials are the foundation of BloodForged. Each material (Bronze, Iron, Steel, etc.)
 * defines physical properties and stats that affect tools made from it.
 * 
 * Materials are:
 * - Data-driven (loaded from JSON files)
 * - Registered in a central registry
 * - Referenced by ID (e.g., "bloodforged:bronze")
 * 
 * This class is the in-memory representation of a material.
 */
public class Material {
    
    // The unique identifier for this material (e.g., "bloodforged:bronze")
    private final Identifier id;
    
    // Display name for UI (e.g., "Bronze")
    private final String displayName;
    
    // The tier/progression level (0 = wood, 1 = copper, 2 = iron, etc.)
    private final int tier;
    
    // Physical properties - these affect how the material behaves
    private final PhysicalProperties properties;
    
    // The color used for rendering (tint for textures)
    private final int color;
    
    /**
     * Constructor - package-private, only MaterialRegistry should create Materials
     */
    Material(Identifier id, String displayName, int tier, PhysicalProperties properties, int color) {
        this.id = Objects.requireNonNull(id, "Material ID cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");
        this.tier = tier;
        this.properties = Objects.requireNonNull(properties, "Physical properties cannot be null");
        this.color = color;
    }
    
    // === GETTERS ===
    
    public Identifier getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getTier() {
        return tier;
    }
    
    public PhysicalProperties getProperties() {
        return properties;
    }
    
    public int getColor() {
        return color;
    }
    
    // === UTILITY ===
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return id.equals(material.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Material{" + id + ", tier=" + tier + "}";
    }
    
    /**
     * Physical properties that define how a material behaves.
     * 
     * CONCEPT:
     * These are NOT tool stats - these are intrinsic material properties.
     * Tool stats are calculated from these properties + part type.
     * 
     * Example:
     * - Hardness affects how sharp edges can be (blade damage)
     * - Flexibility affects handle comfort (attack speed)
     * - Density affects weight (mining speed penalty/bonus)
     */
    public static class PhysicalProperties {
        // How hard the material is (0.0 - 10.0)
        // Harder = better edge retention, more brittle
        private final float hardness;
        
        // How tough/impact-resistant (0.0 - 10.0)
        // Tougher = more durable, absorbs shock better
        private final float toughness;
        
        // How flexible the material is (0.0 - 10.0)
        // More flexible = better handles, worse blades
        private final float flexibility;
        
        // Density/weight (0.0 - 10.0)
        // Higher density = heavier, more damage, slower
        private final float density;
        
        public PhysicalProperties(float hardness, float toughness, float flexibility, float density) {
            this.hardness = clamp(hardness, 0.0f, 10.0f);
            this.toughness = clamp(toughness, 0.0f, 10.0f);
            this.flexibility = clamp(flexibility, 0.0f, 10.0f);
            this.density = clamp(density, 0.0f, 10.0f);
        }
        
        private static float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }
        
        public float getHardness() { return hardness; }
        public float getToughness() { return toughness; }
        public float getFlexibility() { return flexibility; }
        public float getDensity() { return density; }
        
        @Override
        public String toString() {
            return String.format("Props{H:%.1f T:%.1f F:%.1f D:%.1f}", 
                hardness, toughness, flexibility, density);
        }
    }
}
