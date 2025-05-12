package wss.actor.vision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import wss.util.Direction;
import wss.world.Map;

public class RandomVision extends Vision {
    private static final Random rng = new Random();

    @Override
    protected List<VisibleSquare> scan(Map map, int x, int y) {
        List<VisibleSquare> list = new ArrayList<>();
        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        directions.remove(Direction.NONE); // Exclude NONE direction
        Collections.shuffle(directions, rng);
        int numDirections = rng.nextInt(directions.size()) + 1; // At least one direction
        for (int i = 0; i < numDirections; i++) {
            Direction d = directions.get(i);
            int nx = x + d.dx();
            int ny = y + d.dy();
            if (inside(map, nx, ny)) {
                list.add(new VisibleSquare(map.getSquare(nx, ny), 1, d));
            }
        }
        return list;
    }

    private boolean inside(Map m, int x, int y) {
        return x >= 0 && y >= 0 && x < m.getWidth() && y < m.getHeight();
    }
}
