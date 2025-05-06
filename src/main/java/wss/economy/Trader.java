package wss.economy;

import wss.actor.Player;

public class Trader {
    public enum TradeResult { ACCEPT, COUNTER, REJECT }

    private final TraderType type;
    private int patience;
    private final String rewardResource;
    private final int rewardPerGold;

    public Trader(TraderType type) {
        this.type = type;
        this.patience = switch (type) {
            case FRIENDLY -> 4;
            case NEUTRAL  -> 2;
            case GRUMPY   -> 1;
        };
        // Define what each type sells and at what rate:
        switch (type) {
            case FRIENDLY -> { rewardResource = "water"; rewardPerGold = 3; }
            case NEUTRAL  -> { rewardResource = "food";  rewardPerGold = 2; }
            case GRUMPY   -> { rewardResource = "food";  rewardPerGold = 1; }
            default      -> { rewardResource = "food";  rewardPerGold = 1; }
        }
    }

    /** Decides ACCEPT / COUNTER / REJECT based on offer size & patience */
    public TradeResult negotiate(int goldOffered, int foodOffered, int waterOffered) {
        if (goldOffered >= 1) {
            // if they offer ≥1 gold, that's enough to accept
            return TradeResult.ACCEPT;
        }
        if (--patience <= 0) {
            return TradeResult.REJECT;
        }
        return TradeResult.COUNTER;
    }

    /** Called when trader accepts: applies the exchange to the player */
    public void applyReward(Player p, int goldPaid) {
        // subtract the gold
        p.addGold(-goldPaid);

        // give the resource
        int amount = goldPaid * rewardPerGold;
        if (rewardResource.equals("water")) {
            p.addWater(amount);
        } else {
            p.addFood(amount);
        }
        System.out.println("Trader gives you " + amount + " " + rewardResource + " for "
                           + goldPaid + " gold.");
    }

    public TraderType getType() { return type; }
}
