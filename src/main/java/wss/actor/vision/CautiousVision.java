package wss.actor.vision;

import wss.util.Direction;
import wss.world.Map;
//import wss.world.Square;

import java.util.ArrayList;
import java.util.List;

public class CautiousVision extends Vision {
    // Only see squares directly north, south, east, and west

    @Override
    protected List<VisibleSquare> scan(Map map, int x, int y) {
        List<VisibleSquare> list = new ArrayList<>();
        for (Direction d : List.of(Direction.NORTH, Direction.SOUTH,
                                   Direction.EAST,  Direction.WEST)) {
            int nx = x + d.dx(), ny = y + d.dy();
            if (inside(map,nx,ny))
                list.add(new VisibleSquare(map.getSquare(nx,ny),1,d));
        }
        return list;
    }

    private boolean inside(Map m, int x, int y) {
        return x >= 0 && y >= 0 && x < m.getWidth() && y < m.getHeight();
    }
}
