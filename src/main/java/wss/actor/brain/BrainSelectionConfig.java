package wss.actor.brain;

import wss.config.GameConfig;

public class BrainSelectionConfig 
{
    private int survivorStrengthThreshold;
    private int survivorResourceThreshold; 
    private int resourceOptimizerThreshold;
    private int riskTakingDistanceThreshold;
    private int greedyGoldThreshold;
    
    // Constructor with configurable values
    public BrainSelectionConfig() {
        // Try to load from config, fall back to defaults
        this.survivorStrengthThreshold = GameConfig.getIntProperty("brain.survivorStrengthThreshold");
        if (this.survivorStrengthThreshold == 0) this.survivorStrengthThreshold = 5;
        
        this.survivorResourceThreshold = GameConfig.getIntProperty("brain.survivorResourceThreshold");
        if (this.survivorResourceThreshold == 0) this.survivorResourceThreshold = 3;
        
        this.resourceOptimizerThreshold = GameConfig.getIntProperty("brain.resourceOptimizerThreshold");
        if (this.resourceOptimizerThreshold == 0) this.resourceOptimizerThreshold = 6;
        
        this.riskTakingDistanceThreshold = GameConfig.getIntProperty("brain.riskTakingDistanceThreshold");
        if (this.riskTakingDistanceThreshold == 0) this.riskTakingDistanceThreshold = 2;
        
        this.greedyGoldThreshold = GameConfig.getIntProperty("brain.greedyGoldThreshold");
        if (this.greedyGoldThreshold == 0) this.greedyGoldThreshold = 10;
    }
    
    // Getters
    public int getSurvivorStrengthThreshold() {
        return survivorStrengthThreshold;
    }
    
    public int getSurvivorResourceThreshold() {
        return survivorResourceThreshold;
    }
    
    public int getResourceOptimizerThreshold() {
        return resourceOptimizerThreshold;
    }
    
    public int getRiskTakingDistanceThreshold() {
        return riskTakingDistanceThreshold;
    }
    
    public int getGreedyGoldThreshold() {
        return greedyGoldThreshold;
    }
}