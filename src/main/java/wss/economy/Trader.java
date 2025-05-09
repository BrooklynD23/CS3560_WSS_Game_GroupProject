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
        int total = goldOffered + foodOffered + waterOffered;

        if (total >= 2) return TradeResult.ACCEPT;
        if (--patience <= 0) return TradeResult.REJECT;

        return TradeResult.COUNTER;
    }

    /** Called when trader accepts: applies the exchange to the player */
    public void applyReward(Player p, int goldPaid, int foodPaid, int waterPaid) {
        int total = goldPaid + foodPaid + waterPaid;
        int reward = total * rewardPerGold;

        // subtract offered items
        p.addGold(-goldPaid);
        p.addFood(-foodPaid);
        p.addWater(-waterPaid);

        // grant reward
        if (rewardResource.equals("water")) {
            p.addWater(reward);
        } else {
            p.addFood(reward);
        }
        System.out.println("Trader gives you " + reward + " " + rewardResource + " for your offer (G: "
                           + goldPaid + ", F: " + foodPaid + ", W: " + waterPaid + ").");
    }

    public TraderType getType() { return type; }
}
