package wss.world.item;
import wss.actor.Player;
public class FoodBonus extends Item {
    public FoodBonus(int amt) { super(amt, false); }
    @Override public void applyTo(Player p) {
        p.addFood(amount);
        System.out.println("Ate " + amount + " food");
    }
}