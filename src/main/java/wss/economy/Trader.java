package wss.economy;

import wss.actor.Player;

public class Trader {
    public enum TradeResult { ACCEPT, COUNTER, REJECT }

    private final TraderType type;
    private int patience;
    private final TradeStrategy strategy;

    public Trader(TraderType type) {
        this.type = type;
        this.strategy = TradeStrategyFactory.getStrategy(type);
        this.patience = strategy.getInitialPatience();
    }

    /** Decides ACCEPT / COUNTER / REJECT based on offer size & patience */
    public TradeResult negotiate(int goldOffered, int foodOffered, int waterOffered) {
        TradeResult result = strategy.evaluateOffer(goldOffered, foodOffered, waterOffered, patience);
        
        if (result == TradeResult.COUNTER) {
            patience--;
        }
        
        return result;
    }

    /** Called when trader accepts: applies the exchange to the player */
    public void applyReward(Player p, int goldPaid, int foodPaid, int waterPaid) {
        int total = goldPaid + foodPaid + waterPaid;
        int reward = total * strategy.getRewardPerGold();

        // subtract offered items
        p.addGold(-goldPaid);
        p.addFood(-foodPaid);
        p.addWater(-waterPaid);

        // grant reward
        if (strategy.getRewardResource().equals("water")) {
            p.addWater(reward);
        } else {
            p.addFood(reward);
        }
        System.out.println("Trader gives you " + reward + " " + strategy.getRewardResource() + " for your offer (G: "
                           + goldPaid + ", F: " + foodPaid + ", W: " + waterPaid + ").");
    }

    public TraderType getType() { return type; }
    public int getRewardPerGold() { return strategy.getRewardPerGold(); }
    public String getRewardResource() { return strategy.getRewardResource(); }
}
