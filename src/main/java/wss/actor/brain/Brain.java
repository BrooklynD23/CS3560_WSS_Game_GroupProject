package wss.actor.brain;
import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;

public abstract class Brain {
    public abstract Direction makeMove(Player p, Map map);
}