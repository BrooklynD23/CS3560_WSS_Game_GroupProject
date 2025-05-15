package wss.actor;

import java.util.Scanner;
import wss.actor.brain.Brain;
import wss.actor.vision.Vision;
import wss.config.GameConfig;
import wss.economy.Trader;
import wss.util.Direction;
import wss.world.Map;
import wss.world.Square;
import wss.world.item.FoodBonus;
import wss.world.item.GoldBonus;
import wss.world.item.Item;
import wss.world.item.WaterBonus;

public class Player 
{
    // Missing position variables - adding these fixes the error
    private int x;
    private int y;
    
    private int maxStrength = GameConfig.getIntProperty("player.maxStrength");
    private int strength = maxStrength;
    private int maxWater = GameConfig.getIntProperty("player.maxWater");
    private int water = maxWater;
    private int maxFood = GameConfig.getIntProperty("player.maxFood");
    private int food = maxFood;
    private int gold = GameConfig.getIntProperty("player.startingGold");

    private final Brain brain;
    private Vision vision;

    private boolean[][] explored;

    public Player(int startX, int startY, Brain b, Vision v) {
        this.x = startX; 
        this.y = startY;
        this.brain = b;  
        this.vision = v;
    }

    public void initExplorationMap(Map map) {
    explored = new boolean[map.getHeight()][map.getWidth()];
    for (int row = 0; row < map.getHeight(); row++) {
        for (int col = 0; col < map.getWidth(); col++) {
            explored[row][col] = false;
        }
    }
        explored[y][x] = true;
    }   

    public void scaleSuppliesByMapSize(Map map) {
        int baseSupply = 25;
        int minMapArea = 25;
        int maxMapArea = 400;

        int mapArea = map.getWidth() * map.getHeight();
        int clampedArea = Math.max(minMapArea, Math.min(mapArea, maxMapArea));

        double scaleFactor = 1.0 + (double)(clampedArea - minMapArea) / (maxMapArea - minMapArea);

        this.maxStrength = (int)(baseSupply * scaleFactor);
        this.maxWater = (int)(baseSupply * scaleFactor);
        this.maxFood = (int)(baseSupply * scaleFactor);

        this.strength = this.maxStrength;
        this.water = this.maxWater;
        this.food = this.maxFood;
    }

    public void takeTurn(int turn, Map map) {
        // 1) Decide & move
        Direction dir = brain.makeMove(this, map);
        move(dir, map);
        explored[y][x] = true;

        // 2) Info about tile
        Square sq = map.getSquare(x, y);
        //System.out.println("\n[INFO] This location is - Terrain: " + sq.getTerrain().getClass().getSimpleName());

        // 3) Trade logic:  Triggered only if low supplies AND you actually have gold
        if (sq.hasTrader()) {
            System.out.println("\n[trade: INFO] A Trader is here (status: " + sq.getTrader().getType() + ").");

            printMap(map);

            if (food > 8 && water > 8 && strength > 8) { //skip trade only when all supplies are good.
                System.out.println("[trade: INFO] I have enough supplies. Skipping trade.");
            } else if (gold > 0) {
                System.out.println("[trade: ACTION] Supplies low. Attempting trade... - Let's trade!");
                tradeWith(sq.getTrader());
            } else {
                System.out.println("[trade: WARNING] No gold - cannot trade. Skipping trader.");
            }
        }

        // 4) End-of-turn resource drain (AFTER pickup and report) so we eventually run out
        System.out.println("\n[DRAIN]");
        System.out.println("-> Daily survival drain: -1 Strength, -1 Water, -1 Food");
        consume();  // strength--, water--, food--

        // 5) Status report after drain
        System.out.printf("\n[STATUS] [SUMMARY in Turn: %d]%n", turn);
        System.out.printf("-> Position: (%d,%d)%n", x, y);
        System.out.printf("-> Strength: %d%n", strength);
        System.out.printf("-> Food:     %d%n", food);
        System.out.printf("-> Water:    %d%n", water);
        System.out.printf("-> Gold:     %d%n", gold);
        System.out.println("================================================================");

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

        System.out.println("\n[LOCATION]");
        System.out.println("[location - INFO] This location is - Terrain: " + dest.getTerrain().getClass().getSimpleName()); //inform the location of the terrain user encounters


        // 1) Calculate terrain costs
        int mc = dest.getTerrain().getMovementCost();
        int wc = dest.getTerrain().getWaterCost();
        int fc = dest.getTerrain().getFoodCost();
        
        
        // 2) Update location
        x = nx; y = ny;

        // 3) Announce the terrain before applying cost
        System.out.println("\n[TILE] Square terrain: " + dest.getTerrain()
                         + " | Items: " + dest.getItems().size()
                         + " | Trader: " + (dest.hasTrader() ? dest.getTrader().getType() : "none"));        

        //4)  Apply resource costs
        strength -= mc; water -= wc; food -= fc;        

        // 5) Print cost breakdown
        System.out.printf("\n[MOVED] Moved %s to (%d,%d) | ** Cost: [-S%d] [-W%d] [-F%d] ** \n-> Now: [S:%d] [W:%d] [F:%d]%n",
                          d, x, y, mc, wc, fc, strength, water, food);

        // 6) Pickup any items
        if (!dest.getItems().isEmpty()) {
            System.out.println("\n[PICKUPS]");
            
            for (Item it : dest.getItems()) {
                if (it instanceof GoldBonus)  System.out.println("[item: GOLD] I found some gold here!");
                if (it instanceof FoodBonus)  System.out.println("[item: FOOD] I sound some food here!");
                if (it instanceof WaterBonus) System.out.println("[item: WATER] I found some water here!");
                it.applyTo(this);
            }
            dest.getItems().clear();
            // After pickups, print updated resource status
            System.out.printf("[update: INFO] After pickups - Strength: %d | Food: %d | Water: %d | Gold: %d%n",
                                    strength, food, water, gold); 
            
        }

        // 5) Optionally trade
        if (dest.hasTrader()) 
        {
            // the decision to trade or skip happens in takeTurn()
        }
    }

    public void tradeWith(Trader t) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n-------------------- TRADE INITIATED --------------------");
        System.out.printf("[INFO] You currently have - Gold: %d | Food: %d | Water: %d%n%n", gold, food, water); //reprinting amount of resources user has for reference

        //What player OFFERS:
        //Gold offer
        System.out.print("[QUESTION] Enter gold to offer: ");
        int offerGold = Integer.parseInt(sc.nextLine().trim());
        //Food offer
        System.out.print("[QUESTION] Enter food to offer: ");
        int offerFood = Integer.parseInt(sc.nextLine().trim());
        //Water offer
        System.out.print("[QUESTION] Enter water to offer: ");
        int offerWater = Integer.parseInt(sc.nextLine().trim());


        // What player WANTS:
        System.out.print("[QUESTION] What reward do you want? (food or water): ");
        String rewardType = sc.nextLine().trim().toLowerCase();

        while (true) {
            if (offerGold > gold || offerFood > food || offerWater > water) {
                System.out.println("\n[ERROR] Not enough resources for this offer. Trade canceled.");
                return;
            }

            Trader.TradeResult res = t.negotiate(offerGold, offerFood, offerWater);
            System.out.printf("\n[TRADE] Trader (%s) replied: %s%n", t.getType(), res);

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
                    System.out.println("---------------------------------------------------------");
                    return;
                }

                case REJECT -> {
                    System.out.println("[TRADE FAILED] Trader rejected the deal. Trade ended.");
                    System.out.println("---------------------------------------------------------");
                    return;
                }

                case COUNTER -> {
                    System.out.printf("[COUNTER] Trader wants more.");
                    System.out.printf("-> Current offer: Gold: %d, Food: %d, Water: %d%n", offerGold, offerFood, offerWater);

                    //update offer
                    System.out.println("\nAdjust your offer:");
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

    private void printMap(Map map) {
    System.out.println("\n[MAP VIEW]");
    for (int row = 0; row < map.getHeight(); row++) {
        for (int col = 0; col < map.getWidth(); col++) {
            if (x == col && y == row) {
                System.out.print("X ");
            } else if (explored[row][col]) {
                System.out.print("- ");
            } else {
                System.out.print("/ ");
            }
        }
        System.out.println();
    }
    System.out.println();
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

    private void consume() 
    {
    int strengthDrain = GameConfig.getIntProperty("player.strengthDrainRate");
    int waterDrain = GameConfig.getIntProperty("player.waterDrainRate");
    int foodDrain = GameConfig.getIntProperty("player.foodDrainRate");
    
    strength = Math.max(0, strength - strengthDrain);
    water = Math.max(0, water - waterDrain);
    food = Math.max(0, food - foodDrain);
   }

    // helpers for Item.applyTo(...)
    //Adding strength recovery
    public void addFood(int f) {
        food = Math.min(maxFood, food + f);
        strength = Math.min(maxStrength, strength + f / 2); // 1 strength per 2 food 
    }

    public void addWater(int w) {
        water = Math.min(maxWater, water + w);
        strength = Math.min(maxStrength, strength + w / 3); // 1 strength per 3 water
    }

    public void addGold(int g)   { gold     = Math.max(0, gold + g); }

    // getters for Brain & Vision
    public int getX()     { return x; }
    public int getY()     { return y; }
    public int getFood()  { return food; }
    public int getWater() { return water; }
    public int getStrength() { return strength; }
    public int getGold()     { return gold; }
    public Vision getVision() { return vision; }
    public void setVision(Vision v) { this.vision = v; } 
}