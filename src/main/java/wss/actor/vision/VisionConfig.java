package wss.actor.vision;

import java.util.ArrayList;
import java.util.List;
import wss.util.Direction;

public class VisionConfig {
    private int range;
    private List<Direction> allowedDirections;
    
    public VisionConfig(int range, List<Direction> allowedDirections) {
        this.range = range;
        this.allowedDirections = new ArrayList<>(allowedDirections);
    }
    
    public int getRange() {
        return range;
    }
    
    public List<Direction> getAllowedDirections() {
        return new ArrayList<>(allowedDirections);
    }
    
    // Factory methods for common configurations
    public static VisionConfig fullVision(int range) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d != Direction.NONE) {
                dirs.add(d);
            }
        }
        return new VisionConfig(range, dirs);
    }
    
    public static VisionConfig cardinalVision(int range) {
        return new VisionConfig(range, 
            List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
    }
    
    public static VisionConfig diagonalVision(int range) {
        return new VisionConfig(range,
            List.of(Direction.NORTH_EAST, Direction.NORTH_WEST, 
                   Direction.SOUTH_EAST, Direction.SOUTH_WEST));
    }
}
