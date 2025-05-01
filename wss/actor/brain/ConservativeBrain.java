package wss.actor.brain;

import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;

/** Prioritises staying alive > speed */
public class ConservativeBrain extends Brain { 
    @Override
    public Direction makeMove(Player p, Map map) {
        Direction foodDir = p.getVision().closestFood(map,p.getX(),p.getY())
                                      .stream().findFirst().orElse(Direction.EAST);
return foodDir;
                                 // progress
    }
}
