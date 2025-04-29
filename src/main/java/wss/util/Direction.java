package wss.util;

public enum Direction {
    NONE(0,0),
    NORTH(0,-1), SOUTH(0,1), EAST(1,0), WEST(-1,0),
    NORTH_EAST(1,-1), NORTH_WEST(-1,-1),
    SOUTH_EAST(1,1),  SOUTH_WEST(-1,1);

    private final int dx, dy;
    Direction(int dx,int dy){this.dx=dx;this.dy=dy;}
    public int dx(){return dx;} public int dy(){return dy;}
}
