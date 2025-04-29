package wss.world;

import wss.economy.Trader;
import wss.economy.TraderType;
import wss.util.DifficultyLevel;
import wss.world.item.FoodBonus;
import wss.world.item.GoldBonus;
import wss.world.item.Item;
import wss.world.item.WaterBonus;
import wss.world.terrain.*;
import java.util.Random;

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
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Terrain t = switch (rng.nextInt(3)) {
                    case 0 -> new PlainsTerrain();
                    case 1 -> new MountainTerrain();
                    default -> new DesertTerrain();
                };

                Square sq = new Square(t);

                /* ~15 % chance drop random bonus */
                if (rng.nextDouble() < 0.15) {
                    Item bonus = switch (rng.nextInt(3)) {
                        case 0 -> new FoodBonus(3);
                        case 1 -> new WaterBonus(3);
                        default -> new GoldBonus(2);
                    };
                    sq.getItems().add(bonus);
                }

                /* ~5 % chance spawn trader */
                if (rng.nextDouble() < 0.05) {
                    Trader trader = new Trader(
                        rng.nextBoolean() ? TraderType.FRIENDLY : TraderType.NEUTRAL);
                    sq.setTrader(trader);
                }

                grid[y][x] = sq;
            }
        }
    }

    public Square getSquare(int x, int y) { return grid[y][x]; }
    public int getWidth() { return width; }
    public int getHeight(){ return height;}
}
