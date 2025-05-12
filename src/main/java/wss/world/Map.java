package wss.world;

import java.util.Random;
import wss.economy.Trader;
import wss.economy.TraderType;
import wss.util.DifficultyLevel;
import wss.world.item.FoodBonus;
import wss.world.item.GoldBonus;
import wss.world.item.Item;
import wss.world.item.WaterBonus;
import wss.world.terrain.*;

public class Map {
    private final int width, height;
    private final Square[][] grid;

    public Map(int w, int h, DifficultyLevel level) {
        this.width = w; this.height = h;
        grid = new Square[h][w];
        generate(level);
    }

    private void generate(DifficultyLevel level) {
        Random rng = new Random();
    
        double plainsProb, desertProb, mountainProb, swampProb, snowProb; 
        switch (level) {
            case EASY   -> { plainsProb = 0.6; desertProb = 0.2; mountainProb = 0.2; swampProb = 0.1; snowProb = 0.1;}
            case MEDIUM -> { plainsProb = 0.2; desertProb = 0.5; mountainProb = 0.3; swampProb = 0.15; snowProb = 0.15;}
            case HARD   -> { plainsProb = 0.2; desertProb = 0.2; mountainProb = 0.6; swampProb = 0.2; snowProb = 0.2;}
            default    -> { plainsProb = 0.7; desertProb = 0.2; mountainProb = 0.1; swampProb = 0.1; snowProb = 0.1;}
        }
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = rng.nextDouble();
                Terrain t;
                if (r < plainsProb) {
                    t = new PlainsTerrain();
                } else if (r < plainsProb + desertProb) {
                    t = new DesertTerrain();
                } else if (r < plainsProb + desertProb + mountainProb) {
                    t = new MountainTerrain();
                } else if (r < plainsProb + desertProb + mountainProb + swampProb) {
                    t = new SwampTerrain();
                } else {
                    t = new SnowTerrain();
                }
                Square sq = new Square(t);
    
                // 2) ~40% chance of a bonus item
                if (rng.nextDouble() < 0.40) {
                    Item bonus = switch (rng.nextInt(3)) {
                        case 0 -> new FoodBonus(3);
                        case 1 -> new WaterBonus(3);
                        default -> new GoldBonus(2);
                    };
                    sq.getItems().add(bonus);
                }
    
                // 3) ~50% chance of a trader
                if (rng.nextDouble() < 0.50) {
                    TraderType type = TraderType.values()[rng.nextInt(TraderType.values().length)];
                    sq.setTrader(new Trader(type));
                }
    
                grid[y][x] = sq;
            }
        }
    }
    
    public Square getSquare(int x, int y) { return grid[y][x]; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}
