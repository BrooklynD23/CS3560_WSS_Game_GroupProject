package wss.actor.brain;

import wss.actor.Player;
import wss.util.Direction;
import wss.world.Map;
import wss.world.Square;
import wss.world.terrain.DesertTerrain;
import wss.world.terrain.MountainTerrain;

public class SurvivorBrain extends Brain {

    @Override
    public Direction makeMove(Player p, Map map) {
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
            int nx = p.getX() + d.dx();
            int ny = p.getY() + d.dy();
            if (nx < 0 || ny < 0 || nx >= map.getWidth() || ny >= map.getHeight()) continue;

            Square s = map.getSquare(nx, ny);
            if (!(s.getTerrain() instanceof DesertTerrain || s.getTerrain() instanceof MountainTerrain)) {
                return d;  // Prefer plains or safe terrains
            }
        }

        // Fallback: if surrounded by hard terrain or starving, just go east
        return Direction.EAST;
    }
}
