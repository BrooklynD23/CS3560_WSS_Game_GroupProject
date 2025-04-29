package wss.world.item;

import wss.actor.Player;

/** One-time gold pickup */
public class GoldBonus extends Item {

    public GoldBonus(int amount) {
        super(amount, false);
    }

    @Override
    public void applyTo(Player p) {
        p.addGold(amount);
        System.out.println("Picked up " + amount + " gold");
    }
}
