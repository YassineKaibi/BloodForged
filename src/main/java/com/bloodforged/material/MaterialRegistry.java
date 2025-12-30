package com.bloodforged.material;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for all materials in BloodForged.
 * 
 * CONCEPT:
 * This is inspired by Tinkers' Construct's MaterialManager.
 * It's a singleton that stores:
 * - All registered materials
 * - Material stats for each part type
 * - Fast lookup by ID
 * 
 * WHY A REGISTRY?
 * - Mods/datapacks can add materials without touching our code
 * - All materials in one place for easy lookup
 * - Can reload from JSON files
 * 
 * USAGE:
 * MaterialRegistry.INSTANCE.getMaterial(new Identifier("bloodforged", "bronze"))
 */
public class MaterialRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("BloodForged/Materials");
    
    // Singleton instance
    public static final MaterialRegistry INSTANCE = new MaterialRegistry();
    
    // Storage for materials (thread-safe for safety)
    private final Map<Identifier, Material> materials = new ConcurrentHashMap<>();
    
    // Storage for material stats (keyed by: materialId -> partType -> stats)
    private final Map<Identifier, Map<Identifier, MaterialStats>> materialStats = new ConcurrentHashMap<>();
    
    // Private constructor (singleton pattern)
    private MaterialRegistry() {
        LOGGER.info("Initializing Material Registry");
    }
    
    /**
     * Register a new material.
     * 
     * This would normally be called during mod initialization or from JSON loading.
     */
    public void registerMaterial(Material material) {
        Identifier id = material.getId();
        
        if (materials.containsKey(id)) {
            LOGGER.warn("Material {} is already registered! Overwriting...", id);
        }
        
        materials.put(id, material);
        LOGGER.info("Registered material: {} (tier {})", material.getDisplayName(), material.getTier());
    }
    
    /**
     * Register stats for a material + part type combination.
     * 
     * Example: Bronze stats for blade part
     */
    public void registerStats(MaterialStats stats) {
        Identifier materialId = stats.getMaterialId();
        Identifier partType = stats.getPartType();
        
        // Get or create the inner map for this material
        Map<Identifier, MaterialStats> statsForMaterial = materialStats.computeIfAbsent(
            materialId, 
            k -> new ConcurrentHashMap<>()
        );
        
        if (statsForMaterial.containsKey(partType)) {
            LOGGER.warn("Stats for {}/{} already exist! Overwriting...", materialId, partType);
        }
        
        statsForMaterial.put(partType, stats);
        LOGGER.debug("Registered stats: {}/{}", materialId, partType);
    }
    
    /**
     * Get a material by ID.
     * Returns null if not found.
     */
    public Material getMaterial(Identifier id) {
        return materials.get(id);
    }
    
    /**
     * Get stats for a material + part type.
     * Returns null if not found.
     */
    public MaterialStats getStats(Identifier materialId, Identifier partType) {
        Map<Identifier, MaterialStats> statsForMaterial = materialStats.get(materialId);
        if (statsForMaterial == null) {
            return null;
        }
        return statsForMaterial.get(partType);
    }
    
    /**
     * Get all registered materials.
     */
    public Collection<Material> getAllMaterials() {
        return Collections.unmodifiableCollection(materials.values());
    }
    
    /**
     * Get all stats for a specific material.
     */
    public Map<Identifier, MaterialStats> getAllStatsForMaterial(Identifier materialId) {
        Map<Identifier, MaterialStats> stats = materialStats.get(materialId);
        if (stats == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(stats);
    }
    
    /**
     * Check if a material exists.
     */
    public boolean hasMaterial(Identifier id) {
        return materials.containsKey(id);
    }
    
    /**
     * Check if stats exist for a material + part type.
     */
    public boolean hasStats(Identifier materialId, Identifier partType) {
        Map<Identifier, MaterialStats> stats = materialStats.get(materialId);
        return stats != null && stats.containsKey(partType);
    }
    
    /**
     * Clear all materials and stats.
     * Used when reloading from JSON.
     */
    public void clear() {
        LOGGER.info("Clearing material registry");
        materials.clear();
        materialStats.clear();
    }
    
    /**
     * Get registry statistics (for debugging).
     */
    public String getStats() {
        int totalStats = materialStats.values().stream()
            .mapToInt(Map::size)
            .sum();
        
        return String.format("Materials: %d, Total Stats: %d", 
            materials.size(), totalStats);
    }
    
    /**
     * Builder pattern for easy material creation.
     * 
     * USAGE:
     * Material bronze = MaterialRegistry.builder("bloodforged", "bronze")
     *     .displayName("Bronze")
     *     .tier(1)
     *     .properties(6.0f, 5.0f, 4.0f, 7.0f)
     *     .color(0xCD7F32)
     *     .build();
     */
    public static MaterialBuilder builder(String namespace, String path) {
        return new MaterialBuilder(new Identifier(namespace, path));
    }
    
    public static class MaterialBuilder {
        private final Identifier id;
        private String displayName;
        private int tier = 0;
        private Material.PhysicalProperties properties;
        private int color = 0xFFFFFF;
        
        MaterialBuilder(Identifier id) {
            this.id = id;
            this.displayName = id.getPath(); // Default to path
        }
        
        public MaterialBuilder displayName(String name) {
            this.displayName = name;
            return this;
        }
        
        public MaterialBuilder tier(int tier) {
            this.tier = tier;
            return this;
        }
        
        public MaterialBuilder properties(float hardness, float toughness, float flexibility, float density) {
            this.properties = new Material.PhysicalProperties(hardness, toughness, flexibility, density);
            return this;
        }
        
        public MaterialBuilder color(int rgb) {
            this.color = rgb;
            return this;
        }
        
        public Material build() {
            if (properties == null) {
                throw new IllegalStateException("Physical properties must be set!");
            }
            
            Material material = new Material(id, displayName, tier, properties, color);
            // Auto-register
            MaterialRegistry.INSTANCE.registerMaterial(material);
            return material;
        }
    }
}
