package wss.actor.vision;

import wss.util.Direction;
import wss.world.Map;
//import wss.world.Square;

import java.util.ArrayList;
import java.util.List;

public class FullVision extends Vision {

    @Override
    protected List<VisibleSquare> scan(Map map, int x, int y) {
        List<VisibleSquare> list = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;
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
