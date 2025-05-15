package wss.actor.brain;

import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;

/**
 * AdaptiveBrain selects the optimal brain strategy based on the player's current state:
 * - Conservative: default behavior
 * - ResourceOptimizing: when water or food is moderately low
 * - Greedy: when gold is high, maximizing pickup opportunities
 * - RiskTaking: when close to the map edge
 * - Survivor: when in critical condition
 */
public class AdaptiveBrain extends Brain {
    private final Brain conservative        = new ConservativeBrain();
    private final Brain resourceOptimizing  = new ResourceOptimizingBrain();
    private final Brain greedy              = new GreedyBrain();
    private final Brain riskTaking          = new RiskTakingBrain();
    private final Brain survivor            = new SurvivorBrain();
    private final BrainSelectionConfig config = new BrainSelectionConfig();

    @Override
    public Direction makeMove(Player p, Map map) {
        int strength = p.getStrength();
        int food     = p.getFood();
        int water    = p.getWater();
        int gold     = p.getGold();
        int x        = p.getX();
        int goalX    = map.getWidth() - 1;
        int distanceToGoal = goalX - x;

        Brain selected;

        if (strength <= config.getSurvivorStrengthThreshold() || 
            (food <= config.getSurvivorResourceThreshold() && 
             water <= config.getSurvivorResourceThreshold())) {
            selected = survivor;
        } else if (food < config.getResourceOptimizerThreshold() || 
                   water < config.getResourceOptimizerThreshold()) {
            selected = resourceOptimizing;
        } else if (distanceToGoal <= config.getRiskTakingDistanceThreshold()) {
            selected = riskTaking;
        } else if (gold >= config.getGreedyGoldThreshold()) {
            selected = greedy;
        } else {
            selected = conservative;
        }

        System.out.println("[BRAIN] Using strategy: " + selected.getClass().getSimpleName());
        return selected.makeMove(p, map);
    }
}