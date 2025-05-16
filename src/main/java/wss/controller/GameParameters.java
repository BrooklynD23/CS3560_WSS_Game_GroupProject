package wss.controller;

import java.util.function.BiPredicate;
import wss.actor.Player;
import wss.config.GameConfig;
import wss.world.Map;

public class GameParameters {
    public enum GameEndCondition {
        REACHED_GOAL, OUT_OF_RESOURCES, MAX_TURNS_REACHED, CUSTOM_GOAL
    }
    
    private int maxTurns;
    private BiPredicate<Player, Map> customWinCondition;
    
    public GameParameters() {
        this.maxTurns = GameConfig.getIntProperty("game.maxTurns");
        if (this.maxTurns == 0) this.maxTurns = 200; // Default if not specified
        
        // Default win condition (reach eastern edge)
        this.customWinCondition = (player, map) -> player.getX() == map.getWidth() - 1;
    }
    
    public int getMaxTurns() {
        return maxTurns;
    }
    
    public void setMaxTurns(int maxTurns) {
        this.maxTurns = maxTurns;
    }
    
    public BiPredicate<Player, Map> getCustomWinCondition() {
        return customWinCondition;
    }
    
    public void setCustomWinCondition(BiPredicate<Player, Map> customWinCondition) {
        this.customWinCondition = customWinCondition;
    }
    
    // Helper to check if player is out of resources
    public boolean isOutOfResources(Player player) {
        return player.getStrength() <= 0 || player.getWater() <= 0 || player.getFood() <= 0;
    }
}
