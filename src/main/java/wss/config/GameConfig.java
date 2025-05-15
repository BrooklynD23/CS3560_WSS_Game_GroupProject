package wss.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameConfig {
    private static Properties config = new Properties();
    
    static {
        try (InputStream input = GameConfig.class.getClassLoader().getResourceAsStream("game.properties")) {
            if (input != null) {
                config.load(input);
            } else {
                // Load defaults if file not found
                setDefaults();
            }
        } catch (IOException ex) {
            System.err.println("Could not load configuration. Using defaults.");
            setDefaults();
        }
    }
    
    private static void setDefaults() {
        // Player resources
        config.setProperty("player.maxStrength", "25");
        config.setProperty("player.maxWater", "25");
        config.setProperty("player.maxFood", "25");
        config.setProperty("player.startingGold", "10");
        
        // Consumption rates
        config.setProperty("player.strengthDrainRate", "1");
        config.setProperty("player.waterDrainRate", "1");
        config.setProperty("player.foodDrainRate", "1");
        
        // Game settings
        config.setProperty("game.maxTurns", "200");
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(config.getProperty(key, "0"));
    }
    
    public static double getDoubleProperty(String key) {
        return Double.parseDouble(config.getProperty(key, "0"));
    }
    
    public static String getStringProperty(String key) {
        return config.getProperty(key, "");
    }
    
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(config.getProperty(key, "false"));
    }
}