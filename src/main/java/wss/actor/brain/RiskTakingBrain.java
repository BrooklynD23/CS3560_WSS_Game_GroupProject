package wss.actor.brain;

import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;

public class RiskTakingBrain extends Brain {

    @Override
    public Direction makeMove(Player p, Map map) {
        // 80 % push east, 20 % look for food if starving
        if (p.getFood() <= 2 && Math.random() < 0.2) {
            var foodPath = p.getVision().closestFood(map,p.getX(),p.getY());
            if (!foodPath.isEmpty()) return foodPath.get(0);
        }
        return Direction.EAST;
    }
}
