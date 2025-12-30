package com.bloodforged.material;

import net.minecraft.util.Identifier;

/**
 * Example materials for BloodForged.
 * 
 * This shows how to create materials and their stats.
 * In the final implementation, these would come from JSON files.
 * 
 * For now, we're registering them in code to understand the system.
 */
public class ExampleMaterials {
    
    // Part type identifiers
    public static final Identifier PART_BLADE = Identifier.of("bloodforged", "blade");
    public static final Identifier PART_HANDLE = Identifier.of("bloodforged", "handle");
    public static final Identifier PART_GUARD = Identifier.of("bloodforged", "guard");
    
    /**
     * Register all example materials.
     * This would be called during mod initialization.
     */
    public static void register() {
        registerBronze();
        registerIron();
        registerSteel();
    }
    
    /**
     * BRONZE - Early game material
     * 
     * Properties:
     * - Hardness: 6.0 (decent edges, not great)
     * - Toughness: 5.0 (average durability)
     * - Flexibility: 4.0 (somewhat flexible)
     * - Density: 7.0 (moderately heavy)
     * 
     * Good for: Learning the smithing system
     * Tier: 1 (copper/bronze tier)
     */
    private static void registerBronze() {
        Identifier bronzeId = Identifier.of("bloodforged", "bronze");
        
        // Create the material
        Material bronze = MaterialRegistry.builder("bloodforged", "bronze")
            .displayName("Bronze")
            .tier(1)
            .properties(6.0f, 5.0f, 4.0f, 7.0f)
            .color(0xCD7F32) // Bronze color
            .build();
        
        // Register blade stats
        MaterialStats bladeStats = new MaterialStats(
            bronzeId, PART_BLADE,
            250,    // durability
            2.5f,   // attack damage
            5.0f,   // mining speed
            1       // mining level (stone)
        );
        MaterialRegistry.INSTANCE.registerStats(bladeStats);
        
        // Register handle stats
        MaterialStats handleStats = new MaterialStats(
            bronzeId, PART_HANDLE,
            0,      // durability (handles don't add base durability)
            0,      // attack damage
            0,      // mining speed
            1.1f,   // attack speed multiplier (10% faster)
            0       // mining level
        );
        MaterialRegistry.INSTANCE.registerStats(handleStats);
        
        // Register guard stats
        MaterialStats guardStats = new MaterialStats(
            bronzeId, PART_GUARD,
            100,    // durability bonus
            0.5f,   // small attack bonus
            0,      // no mining speed
            1.0f,   // normal attack speed
            0       // no mining level
        );
        MaterialRegistry.INSTANCE.registerStats(guardStats);
    }
    
    /**
     * IRON - Mid-tier material
     * 
     * Properties:
     * - Hardness: 7.5 (sharp edges)
     * - Toughness: 6.0 (good durability)
     * - Flexibility: 3.0 (somewhat rigid)
     * - Density: 7.5 (heavy)
     * 
     * Trade-off: Better than bronze, but rusts (trait system later)
     * Tier: 2 (iron tier)
     */
    private static void registerIron() {
        Identifier ironId = Identifier.of("bloodforged", "iron");
        
        Material iron = MaterialRegistry.builder("bloodforged", "iron")
            .displayName("Iron")
            .tier(2)
            .properties(7.5f, 6.0f, 3.0f, 7.5f)
            .color(0xD8D8D8) // Light gray
            .build();
        
        // Blade: Better damage and durability than bronze
        MaterialStats bladeStats = new MaterialStats(
            ironId, PART_BLADE,
            400,    // +60% durability vs bronze
            3.5f,   // +40% damage vs bronze
            6.0f,   // +20% mining speed vs bronze
            2       // Can mine diamond
        );
        MaterialRegistry.INSTANCE.registerStats(bladeStats);
        
        // Handle: Heavier, slightly slower
        MaterialStats handleStats = new MaterialStats(
            ironId, PART_HANDLE,
            0, 0, 0,
            1.0f,   // Normal speed (bronze was 1.1f)
            0
        );
        MaterialRegistry.INSTANCE.registerStats(handleStats);
        
        // Guard: Much more protective
        MaterialStats guardStats = new MaterialStats(
            ironId, PART_GUARD,
            150,    // +50% vs bronze
            1.0f,   // Double the attack bonus vs bronze
            0, 1.0f, 0
        );
        MaterialRegistry.INSTANCE.registerStats(guardStats);
    }
    
    /**
     * STEEL - High-tier material
     * 
     * Properties:
     * - Hardness: 8.5 (very sharp)
     * - Toughness: 8.0 (excellent durability)
     * - Flexibility: 4.0 (balanced)
     * - Density: 7.8 (heavy but refined)
     * 
     * Requires: Alloying iron + carbon (blast furnace)
     * Tier: 3 (diamond tier)
     */
    private static void registerSteel() {
        Identifier steelId = Identifier.of("bloodforged", "steel");
        
        Material steel = MaterialRegistry.builder("bloodforged", "steel")
            .displayName("Steel")
            .tier(3)
            .properties(8.5f, 8.0f, 4.0f, 7.8f)
            .color(0x808080) // Dark gray
            .build();
        
        // Blade: Superior in every way
        MaterialStats bladeStats = new MaterialStats(
            steelId, PART_BLADE,
            600,    // +50% vs iron
            5.0f,   // +43% vs iron
            8.0f,   // +33% vs iron
            3       // Can mine netherite
        );
        MaterialRegistry.INSTANCE.registerStats(bladeStats);
        
        // Handle: Well-balanced
        MaterialStats handleStats = new MaterialStats(
            steelId, PART_HANDLE,
            0, 0, 0,
            1.15f,  // Slight speed bonus
            0
        );
        MaterialRegistry.INSTANCE.registerStats(handleStats);
        
        // Guard: Excellent protection
        MaterialStats guardStats = new MaterialStats(
            steelId, PART_GUARD,
            250,    // +67% vs iron
            1.5f,   // +50% vs iron
            0, 1.0f, 0
        );
        MaterialRegistry.INSTANCE.registerStats(guardStats);
    }
    
    /**
     * Example: Calculate stats for a full weapon
     */
    public static void exampleCalculation() {
        // Let's say we make a sword with:
        // - Steel blade (quality 85%)
        // - Bronze handle (quality 70%)
        // - Iron guard (quality 90%)
        
        Identifier steelId = Identifier.of("bloodforged", "steel");
        Identifier bronzeId = Identifier.of("bloodforged", "bronze");
        Identifier ironId = Identifier.of("bloodforged", "iron");
        
        // Get base stats
        MaterialStats bladeStat = MaterialRegistry.INSTANCE.getStats(steelId, PART_BLADE);
        MaterialStats handleStat = MaterialRegistry.INSTANCE.getStats(bronzeId, PART_HANDLE);
        MaterialStats guardStat = MaterialRegistry.INSTANCE.getStats(ironId, PART_GUARD);
        
        // Apply quality
        MaterialStats bladeWithQuality = bladeStat.withQuality(85);
        MaterialStats handleWithQuality = handleStat.withQuality(70);
        MaterialStats guardWithQuality = guardStat.withQuality(90);
        
        // Combine stats (simple addition for now)
        int totalDurability = bladeWithQuality.getDurability() 
            + guardWithQuality.getDurability();
        
        float totalDamage = bladeWithQuality.getAttackDamage() 
            + guardWithQuality.getAttackDamage();
        
        float attackSpeed = handleWithQuality.getAttackSpeed();
        
        System.out.println("=== Crafted Sword Stats ===");
        System.out.println("Durability: " + totalDurability);
        System.out.println("Attack Damage: " + totalDamage);
        System.out.println("Attack Speed: " + attackSpeed);
        System.out.println("Mining Level: " + bladeWithQuality.getMiningLevel());
    }
}
