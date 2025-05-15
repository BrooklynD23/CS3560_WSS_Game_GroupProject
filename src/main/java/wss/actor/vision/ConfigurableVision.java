package wss.actor.vision;

import java.util.ArrayList;
import java.util.List;
import wss.util.Direction;
import wss.world.Map;
import wss.world.Square;

public class ConfigurableVision extends Vision {
    private final VisionConfig config;
    
    public ConfigurableVision(VisionConfig config) {
        this.config = config;
    }
    
    @Override
    protected List<VisibleSquare> scan(Map map, int x, int y) {
        List<VisibleSquare> visible = new ArrayList<>();
        for (Direction d : config.getAllowedDirections()) {
            for (int i = 1; i <= config.getRange(); i++) {
                int nx = x + (d.dx() * i);
                int ny = y + (d.dy() * i);
                if (inside(map, nx, ny)) {
                    visible.add(new VisibleSquare(map.getSquare(nx, ny), i, d));
    
                }
            }
        }
        return visible;
    }
    
    private boolean inside(Map m, int x, int y) {
        return x >= 0 && y >= 0 && x < m.getWidth() && y < m.getHeight();
    }
}