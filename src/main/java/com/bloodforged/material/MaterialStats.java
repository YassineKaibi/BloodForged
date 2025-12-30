package com.bloodforged.material;

import net.minecraft.util.Identifier;

/**
 * Defines the stats a material provides when used for a specific tool part.
 * 
 * CONCEPT:
 * The same material has DIFFERENT stats depending on which part you make:
 * - Bronze BLADE: high hardness → good damage
 * - Bronze HANDLE: high flexibility → good attack speed
 * - Bronze GUARD: high toughness → good durability
 * 
 * This is why Tinkers' Construct has separate stat files per part type!
 * 
 * EXAMPLE DATA STRUCTURE (JSON):
 * {
 *   "material": "bloodforged:bronze",
 *   "part_type": "bloodforged:blade",
 *   "stats": {
 *     "durability": 250,
 *     "attack_damage": 2.5,
 *     "mining_speed": 5.0
 *   }
 * }
 */
public class MaterialStats {
    
    // Which material these stats are for
    private final Identifier materialId;
    
    // Which part type these stats apply to (blade, handle, guard, etc.)
    private final Identifier partType;
    
    // Base durability contribution
    private final int durability;
    
    // Attack damage contribution
    private final float attackDamage;
    
    // Mining speed contribution
    private final float miningSpeed;
    
    // Attack speed modifier (1.0 = normal, higher = faster)
    private final float attackSpeed;
    
    // Mining level (0 = wood, 1 = stone, 2 = iron, 3 = diamond, 4 = netherite)
    private final int miningLevel;
    
    /**
     * Constructor for blade/head parts (focus on damage + mining)
     */
    public MaterialStats(Identifier materialId, Identifier partType, 
                        int durability, float attackDamage, float miningSpeed, int miningLevel) {
        this.materialId = materialId;
        this.partType = partType;
        this.durability = durability;
        this.attackDamage = attackDamage;
        this.miningSpeed = miningSpeed;
        this.attackSpeed = 1.0f; // Default
        this.miningLevel = miningLevel;
    }
    
    /**
     * Constructor for handle parts (focus on multipliers + speed)
     */
    public MaterialStats(Identifier materialId, Identifier partType,
                        float durabilityMultiplier, float attackSpeed) {
        this.materialId = materialId;
        this.partType = partType;
        this.durability = 0; // Handles don't add base durability
        this.attackDamage = 0; // Handles don't add base damage
        this.miningSpeed = 0; // Handles don't add base mining speed
        this.attackSpeed = attackSpeed;
        this.miningLevel = 0;
        
        // Note: Multipliers would be stored separately in a real implementation
        // For now we'll keep it simple
    }
    
    /**
     * Full constructor for maximum flexibility
     */
    public MaterialStats(Identifier materialId, Identifier partType,
                        int durability, float attackDamage, float miningSpeed, 
                        float attackSpeed, int miningLevel) {
        this.materialId = materialId;
        this.partType = partType;
        this.durability = durability;
        this.attackDamage = attackDamage;
        this.miningSpeed = miningSpeed;
        this.attackSpeed = attackSpeed;
        this.miningLevel = miningLevel;
    }
    
    // === GETTERS ===
    
    public Identifier getMaterialId() {
        return materialId;
    }
    
    public Identifier getPartType() {
        return partType;
    }
    
    public int getDurability() {
        return durability;
    }
    
    public float getAttackDamage() {
        return attackDamage;
    }
    
    public float getMiningSpeed() {
        return miningSpeed;
    }
    
    public float getAttackSpeed() {
        return attackSpeed;
    }
    
    public int getMiningLevel() {
        return miningLevel;
    }
    
    /**
     * Apply quality modifier to these stats.
     * 
     * CONCEPT:
     * When you smith a part, you get a quality % (0-100%).
     * This quality scales the base stats:
     * - 50% quality = 50% of base stats
     * - 100% quality = 100% of base stats
     * - 120% quality = 120% of base stats (master smithing bonus)
     * 
     * This is BloodForged's unique twist on Tinkers' system!
     */
    public MaterialStats withQuality(float qualityPercent) {
        float multiplier = qualityPercent / 100.0f;
        
        return new MaterialStats(
            materialId,
            partType,
            Math.round(durability * multiplier),
            attackDamage * multiplier,
            miningSpeed * multiplier,
            attackSpeed, // Attack speed isn't affected by quality
            miningLevel  // Mining level isn't affected by quality
        );
    }
    
    @Override
    public String toString() {
        return String.format("Stats{%s/%s: dur=%d, dmg=%.1f, speed=%.1f}", 
            materialId, partType, durability, attackDamage, miningSpeed);
    }
}
