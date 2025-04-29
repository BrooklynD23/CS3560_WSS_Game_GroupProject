package wss.economy;

//import java.util.Random;

public class Trader {

    private final TraderType type;
    private int patience;          // counters before quitting

    public Trader(TraderType type) {
        this.type = type;
        this.patience = switch (type) {
            case FRIENDLY -> 4;
            case NEUTRAL  -> 2;
            case GRUMPY   -> 1;
        };
    }

    public TradeResult negotiate(int goldOffered, int foodOffered, int waterOffered) {
        /* Accept rule: offer must contain at least 1 resource we "like" */
        boolean acceptable = (goldOffered + foodOffered + waterOffered) >= 2;

        if (acceptable) return TradeResult.ACCEPT;

        if (--patience <= 0) return TradeResult.REJECT;

        // simple counter-offer – request +1 gold
        return TradeResult.COUNTER;
    }

    public enum TradeResult { ACCEPT, COUNTER, REJECT }
    public TraderType getType() { return type; }
}
