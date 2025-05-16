package wss.world.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import wss.util.DifficultyLevel;

public class TerrainDistribution {
    private Map<Class<? extends Terrain>, Double> probabilities = new HashMap<>();
    private Random random = new Random();
    
    public static TerrainDistribution forDifficulty(DifficultyLevel level) {
        TerrainDistribution dist = new TerrainDistribution();
        
        switch (level) {
            case EASY:
                dist.setProbability(PlainsTerrain.class, 0.6);
                dist.setProbability(DesertTerrain.class, 0.2);
                dist.setProbability(MountainTerrain.class, 0.2);
                dist.setProbability(SwampTerrain.class, 0.1);
                dist.setProbability(SnowTerrain.class, 0.1);
                break;
            case MEDIUM:
                dist.setProbability(PlainsTerrain.class, 0.2);
                dist.setProbability(DesertTerrain.class, 0.5);
                dist.setProbability(MountainTerrain.class, 0.3);
                dist.setProbability(SwampTerrain.class, 0.15);
                dist.setProbability(SnowTerrain.class, 0.15);
                break;
            case HARD:
                dist.setProbability(PlainsTerrain.class, 0.2);
                dist.setProbability(DesertTerrain.class, 0.2);
                dist.setProbability(MountainTerrain.class, 0.6);
                dist.setProbability(SwampTerrain.class, 0.2);
                dist.setProbability(SnowTerrain.class, 0.2);
                break;
            default:
                dist.setProbability(PlainsTerrain.class, 0.7);
                dist.setProbability(DesertTerrain.class, 0.2);
                dist.setProbability(MountainTerrain.class, 0.1);
                dist.setProbability(SwampTerrain.class, 0.1);
                dist.setProbability(SnowTerrain.class, 0.1);
                break;
        }
        
        return dist;
    }
    
    public void setProbability(Class<? extends Terrain> terrainClass, double probability) {
        probabilities.put(terrainClass, probability);
    }
    
    public double getProbability(Class<? extends Terrain> terrainClass) {
        return probabilities.getOrDefault(terrainClass, 0.0);
    }
    

    
/**
 * Purpose: to select and instantiate a random terrain type based on configured probabilities values.
 * 
 * 
 * 1. Generates a random value between 0.0 and 1.0
 * 2. Iterates through terrain types, accumulating their probability values
 * 3. When the accumulated probability exceeds the random value, selects that terrain
 *
 * The algorithm ensures that terrain types with higher probability values are
 * selected more frequently. For example, if PlainsTerrain has 0.6 probability and
 * MountainTerrain has 0.2, then Plains will appear approximately 3 times as often.
 *
 * Error handling: If instantiation fails for any reason, defaults to PlainsTerrain
 * as a safe fallback option.
 *
 * @return Terrain A new instance of the randomly selected terrain type  **/ 

    public Terrain getRandomTerrain() {
        double r = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        // Sort terrain types by probability for more deterministic generation
        List<Class<? extends Terrain>> terrainTypes = new ArrayList<>(probabilities.keySet());
        
        for (Class<? extends Terrain> terrainClass : terrainTypes) {
            cumulativeProbability += probabilities.get(terrainClass);
            if (r < cumulativeProbability) {
                try {
                    return terrainClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Error creating terrain: " + e.getMessage());
                    return new PlainsTerrain(); // Fallback
                }
            }
        }
        
        // Default fallback
        return new PlainsTerrain();
    }
}
