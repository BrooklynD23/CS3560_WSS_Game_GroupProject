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