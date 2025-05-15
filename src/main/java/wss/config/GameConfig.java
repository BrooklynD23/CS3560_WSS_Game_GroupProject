package wss.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Centralized game configuration management system.
 * 
 * This class loads and provides access to configurable game parameters from
 * an external properties file. The configuration system has: 
 * 
 * - Automatic loading on application startup
 * - Backup to default values if the config file is missing
 * - Type-safe accessor methods for different value types
 * - Centralized default value definitions
 * 
 * Configuration is initialized once when the class is first referenced and
 * remains constant throughout program execution
 */
public class GameConfig {
    private static Properties config = new Properties();
    
    /**
     * Static initializer passes configuration when the class is first accessed.
     * 
     * Sequence:
     * 1. Attempts to load from "game.properties" in the classpath
     * 2. If successful, uses provided values
     * 3. If file is missing or invalid, falls back to default values
     * 
     */

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

    /**
     * Defines default values for all configurable game parameters.
     * 
     * These values are used when the external configuration file
     * cannot be loaded or is missing specific properties.
     * 
     * ** This is the initial values we had for the program to work. 
     *  However, we wanted to create an easier system to handle different configurations.
     */
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