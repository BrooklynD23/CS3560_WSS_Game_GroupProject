package wss.actor;

import java.util.Scanner;
import wss.actor.brain.Brain;
import wss.actor.vision.Vision;
import wss.economy.Trader;
import wss.util.Direction;
import wss.world.Map;
import wss.world.Square;
import wss.world.item.FoodBonus;
import wss.world.item.GoldBonus;
import wss.world.item.Item;
import wss.world.item.WaterBonus;

public class Player {
    private int x, y;
    private int maxStrength = 20, strength = 20;
    private int maxWater    = 20,    water    = 20;
    private int maxFood     = 20,    food     = 20;
    private int gold        = 5;

    private final Brain   brain;
    private Vision  vision;

    public Player(int startX, int startY, Brain b, Vision v) {
        this.x = startX; 
        this.y = startY;
        this.brain = b;  
        this.vision = v;
    }



    public void takeTurn(Map map) {
        // 1) Decide & move
        Direction dir = brain.makeMove(this, map);
        move(dir, map);

        // 2) Info about tile
        Square sq = map.getSquare(x, y);
        System.out.println("\n[INFO] This location is - Terrain: " + sq.getTerrain().getClass().getSimpleName());

        // 3) Trade logic:  Triggered only if low supplies AND you actually have gold
        if (sq.hasTrader()) {
            System.out.println("\n[INFO] A Trader is here (" + sq.getTrader().getType() + ").");
            if (food > 3 && water > 3) {
                System.out.println("[INFO] I have enough supplies. Skipping trade.");
            } else if (gold > 0) {
                System.out.println("[ACTION] Supplies low. Attempting trade... - Let's trade!");
                tradeWith(sq.getTrader());
            } else {
                System.out.println("[WARNING] No gold - cannot trade. Skipping trader.");
            }
        }

        // 4) End-of-turn resource drain (AFTER pickup and report) so we eventually run out
        System.out.println("\n[DRAIN]");
        System.out.println("-> Daily survival drain: -1 Strength, -1 Water, -1 Food");
        consume();  // strength--, water--, food--

        // 5) Status report after drain
        System.out.println("\n[STATUS]");
        System.out.printf("-> Position: (%d,%d)%n", x, y);
        System.out.printf("-> Strength: %d%n", strength);
        System.out.printf("-> Food:     %d%n", food);
        System.out.printf("-> Water:    %d%n", water);
        System.out.printf("-> Gold:     %d%n", gold);
        System.out.println("------------------------------");

        System.out.println("--Turn ends.--\n");
    }


    private void move(Direction d, Map map) {
        int nx = x + d.dx(), ny = y + d.dy();
        if (!inside(map, nx, ny)) {
            System.out.println("[BLOCKED] Move " + d + " blocked (out of bounds).");
            return;
        }

        Square dest = map.getSquare(nx, ny);
        if (!canEnter(dest)) {
            System.out.println("[BLOCKED] Not enough resources to move " + d);
            return;
        }

        // 1) Deduct terrain costs
        System.out.println("\n[MOVE]");
        int mc = dest.getTerrain().getMovementCost();
        int wc = dest.getTerrain().getWaterCost();
        int fc = dest.getTerrain().getFoodCost();
        
        strength -= mc; water -= wc; food -= fc;

        // 2) Update location
        x = nx; y = ny;

        // 3) Print cost breakdown
        System.out.printf("[MOVE] Moved %s to (%d,%d) | **! Cost: [-S%d] [-W%d] [-F%d] !** \n-> Now: [S:%d] [W:%d] [F:%d]%n",
                          d, x, y, mc, wc, fc, strength, water, food);

        // 4) Describe square contents
        System.out.println("\n[TILE] Square terrain: " + dest.getTerrain()
                         + " | Items: " + dest.getItems().size()
                         + " | Trader: " + (dest.hasTrader() ? dest.getTrader().getType() : "none"));

        // 5) Pickup any items
        if (!dest.getItems().isEmpty()) {
            System.out.println("\n[PICKUPS]");
         }   
        for (Item it : dest.getItems()) {
            if (it instanceof GoldBonus)  System.out.println("[GOLD] I found some gold here!");
            if (it instanceof FoodBonus)  System.out.println("[FOOD] I sound some food here!");
            if (it instanceof WaterBonus) System.out.println("[WATER] I found some water here!");
            it.applyTo(this);
        }
        dest.getItems().clear();


        // 7) Optionally trade
        if (dest.hasTrader()) {
            // the decision to trade or skip happens in takeTurn()
        }
    }

    public void tradeWith(Trader t) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- TRADE INITIATED ---");

        //What player OFFERS:
        //Gold offer
        System.out.println("[QUESTION] Enter gold to offer: ");
        int offerGold = Integer.parseInt(sc.nextLine().trim());
        //Food offer
        System.out.print("[QUESTION] Enter food to offer: ");
        int offerFood = Integer.parseInt(sc.nextLine().trim());
        //Water offer
        System.out.print("[QUESTION] Enter water to offer: ");
        int offerWater = Integer.parseInt(sc.nextLine().trim());


        // What player WANTS:
        System.out.println("[QUESTION] What reward do you want? (food or water): ");
        String rewardType = sc.nextLine().trim().toLowerCase();

        while (true) {
            if (offerGold > gold || offerFood > food || offerWater > water) {
                System.out.println("[ERROR] Not enough resources for this offer. Trade canceled.");
                return;
            }

            Trader.TradeResult res = t.negotiate(offerGold, offerFood, offerWater);
            System.out.printf("[TRADE] Trader (%s) replied: %s%n", t.getType(), res);

            switch (res) {
                case ACCEPT -> {
                    gold -= offerGold;
                    food -= offerFood;
                    water -= offerWater;

                    int reward = (offerGold + offerFood + offerWater) * t.getRewardPerGold();

                    if (rewardType.equals("water")){
                        addWater(reward);
                    } else {
                        addFood(reward);
                    }

                    System.out.println("[TRADE SUCCESS] Trader gives you " + reward + " " + rewardType + ".");
                    return;
                }

                case REJECT -> {
                    System.out.println("[TRADE FAILED] Trader rejected the deal. Trade ended.");
                    return;
                }

                case COUNTER -> {
                    System.out.printf("[COUNTER] Trader wants more.");
                    System.out.printf("→ Current offer: Gold: %d, Food: %d, Water: %d%n", offerGold, offerFood, offerWater);

                    //update offer
                    System.out.println("Adjust your offer:");
                    System.out.print("[INPUT] New offer - Increase gold to: ");
                    offerGold = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("[INPUT] New offer - Increase food to: ");
                    offerFood = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("[INPUT] New offer - Increase water to: ");
                    offerWater = Integer.parseInt(sc.nextLine().trim());
                }
            }
        }
    }




    private boolean canEnter(Square s) {
        var t = s.getTerrain();
        return strength >= t.getMovementCost()
            && water    >= t.getWaterCost()
            && food     >= t.getFoodCost();
    }

    private boolean inside(Map m, int x, int y) {
        return x>=0 && y>=0 && x<m.getWidth() && y<m.getHeight();
    }

    private void consume() {
        strength = Math.max(0, strength - 1);
        water = Math.max(0, water - 1);
        food = Math.max(0, food - 1);
    }

    // helpers for Item.applyTo(...)
    public void addFood(int f)   { food     = Math.min(maxFood, food + f); }
    public void addWater(int w)  { water    = Math.min(maxWater, water + w); }
    public void addGold(int g)   { gold     = Math.max(0, gold + g); }

    // getters for Brain & Vision
    public int getX()     { return x; }
    public int getY()     { return y; }
    public int getFood()  { return food; }
    public int getWater() { return water; }
    public int getStrength() { return strength; }
    public int getGold()     { return gold; }
    public Vision getVision() { return vision; }
    public void setVision(Vision v) { this.vision = v; } // Optional for switching vision
}
