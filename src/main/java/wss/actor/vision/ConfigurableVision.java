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

/** Scans the surrounding area based on the vision configuration.
 *
 * This method dynamically creates a list of visible squares by:
 * 1. Iterating through each allowed direction in the configuration
 * 2. Looking outward from the player's position up to the configured range
 * 3. Collecting information about each visible square (terrain, items, distance)
 *
 * @param map The game map containing all squares
 * @param x The current x-coordinate of the player
 * @param y The current y-coordinate of the player
 * @return List<VisibleSquare> All squares visible from the player's position **/

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