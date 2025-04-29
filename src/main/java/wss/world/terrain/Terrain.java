package wss.world.terrain;

public abstract class Terrain {
    protected final int movementCost;
    protected final int waterCost;
    protected final int foodCost;

    protected Terrain(int m, int w, int f) {
        this.movementCost = m;
        this.waterCost    = w;
        this.foodCost     = f;
    }
    public int getMovementCost() { return movementCost; }
    public int getWaterCost()    { return waterCost; }
    public int getFoodCost()     { return foodCost; }

    @Override public String toString() {
        return getClass().getSimpleName();
    }
}