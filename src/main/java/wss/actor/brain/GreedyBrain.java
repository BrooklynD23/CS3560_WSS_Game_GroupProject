package wss.actor.brain;

import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;

import java.util.List;

public class GreedyBrain extends Brain {
    @Override
    public Direction makeMove(Player p, Map map) {
        List<Direction> gold = p.getVision().closestGold(map, p.getX(), p.getY());
        if (!gold.isEmpty()) return gold.get(0);

        if (p.getWater() <= 5) {
            List<Direction> water = p.getVision().closestWater(map, p.getX(), p.getY());
            if (!water.isEmpty()) return water.get(0);
        }

        if (p.getFood() <= 5) {
            List<Direction> food = p.getVision().closestFood(map, p.getX(), p.getY());
            if (!food.isEmpty()) return food.get(0);
        }

        return Direction.EAST;
    }
}
