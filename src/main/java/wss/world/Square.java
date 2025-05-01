package wss.world;

import wss.world.terrain.Terrain;
import wss.world.item.Item;
import wss.economy.Trader;
import java.util.ArrayList;
import java.util.List;

public class Square {
    private final Terrain terrain;
    private final List<Item> items = new ArrayList<>();
    private Trader trader;

    public Square(Terrain t) { this.terrain = t; }
    // getters
    public Terrain getTerrain()      { return terrain; }
    public List<Item> getItems()     { return items; }
    public boolean hasTrader()       { return trader != null; }
    public Trader getTrader()        { return trader; }
    public void setTrader(Trader tr) { this.trader = tr; }
}
