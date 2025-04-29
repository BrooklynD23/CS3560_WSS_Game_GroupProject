package wss.world.item;

import wss.actor.Player;

/** Water replenishment (non-repeating by default) */
public class WaterBonus extends Item {

    public WaterBonus(int amount) {
        super(amount, false);
    }

    @Override
    public void applyTo(Player p) {
        p.addWater(amount);
        System.out.println("Drank " + amount + " water");
    }
}
