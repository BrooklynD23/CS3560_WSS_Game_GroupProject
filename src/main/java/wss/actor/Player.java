package wss.actor;

import wss.actor.brain.Brain;
import wss.actor.vision.Vision;
import wss.economy.Trader;
import wss.util.Direction;

public class Player {
    private int x, y;
    private int maxStrength = 10, strength = 10;
    private int maxWater = 10,    water    = 10;
    private int maxFood  = 10,    food     = 10;
    private int gold     = 0;

    private final Brain brain;
    private final Vision vision;

    public Player(int startX, int startY, Brain b, Vision v) {
        this.x = startX; this.y = startY;
        this.brain = b;  this.vision = v;
    }

    public void takeTurn(wss.world.Map map) {
        System.out.printf("Turn stats — Pos(%d,%d) Str:%d Water:%d Food:%d Gold:%d%n",
                x, y, strength, water, food, gold);
    
        Direction dir = brain.makeMove(this, map);
        System.out.println("Brain chose to move " + dir);
    
        move(dir, map);
    
        consume();
    }

    // inside Player.java
    private boolean canEnter(wss.world.Square s) {
        var t = s.getTerrain();
        return strength  >= t.getMovementCost()
            && water     >= t.getWaterCost()
            && food      >= t.getFoodCost();
    }

    private void move(Direction d, wss.world.Map map) {
        int nx = x + d.dx();
        int ny = y + d.dy();
        if (!inside(map, nx, ny)) {
            System.out.println("Move blocked (out of bounds)");
            return;
        }
    
        var destination = map.getSquare(nx, ny);
        if (!canEnter(destination)) {
            System.out.println("Not enough resources to move " + d);
            return;
        }
    
        int mc = destination.getTerrain().getMovementCost();
        int wc = destination.getTerrain().getWaterCost();
        int fc = destination.getTerrain().getFoodCost();
    
        // deduct terrain cost
        strength -= mc;
        water    -= wc;
        food     -= fc;
    
        x = nx; y = ny;
        System.out.printf("Moved %s to (%d,%d) — Cost: -S%d -W%d -F%d → Now S:%d W:%d F:%d%n",
                d, x, y, mc, wc, fc, strength, water, food);
    
        // After moving, check what is on the square
        System.out.println("Arrived on " + destination.getTerrain()
                + " — Items: " + destination.getItems().size()
                + " Trader: " + (destination.hasTrader() ? destination.getTrader().getType() : "none"));
    
        // Pickup items
        if (!destination.getItems().isEmpty()) {
            for (var item : destination.getItems()) {
                item.applyTo(this);
            }
            destination.getItems().clear(); // clear after pickup
        }
    
        // Optional: Trade if trader exists
        if (destination.hasTrader()) {
            tradeWith(destination.getTrader());
        }
    }
    private boolean inside(wss.world.Map m,int x,int y){
        return x>=0&&y>=0&&x<m.getWidth()&&y<m.getHeight();
    }

    public void tradeWith(Trader t) {
    if (t == null) return;
    int offerGold = 0, offerFood = 0, offerWater = 1;   // trivial first offer

    while (true) {
        Trader.TradeResult res = t.negotiate(offerGold, offerFood, offerWater);
        System.out.println("Trader ("+t.getType()+") replied: "+res);
        switch (res) {
            case ACCEPT -> {
                water += offerWater;
                food  -= offerFood;
                gold  -= offerGold;
                return;
            }
            case REJECT -> { System.out.println("Trade ended."); return; }
            case COUNTER -> { offerGold++; }   // raise gold, loop again
        }
    }
}


    private void consume() { strength--; water--; food--; }

    // simple helpers used by Item
    public void addFood(int f)  { food = Math.min(maxFood, food+f); }
    public void addWater(int w) { water= Math.min(maxWater, water+w);}
    public void addGold(int g)  { gold += g; }

    // getters
    public int getX(){return x;} public int getY(){return y;}

    public int getFood()     { return food; }
    public int getWater()    { return water; }
    public int getStrength() { return strength; }
    public Vision getVision(){ return vision; }
}
