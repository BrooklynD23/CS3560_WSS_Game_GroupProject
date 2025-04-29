package wss.world.item;

import wss.actor.Player;

public abstract class Item {
    protected final int amount;
    protected final boolean repeating;
    protected Item(int amt, boolean repeat) {
        this.amount = amt; this.repeating = repeat;
    }
    public abstract void applyTo(Player p);
    public boolean isFood() { return this instanceof FoodBonus; }
    public boolean isWater() { return this instanceof WaterBonus; }
    public boolean isGold() { return this instanceof GoldBonus; }
}