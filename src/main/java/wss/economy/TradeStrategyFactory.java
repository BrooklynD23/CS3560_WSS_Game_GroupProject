package wss.economy;

public class TradeStrategyFactory {
    public static TradeStrategy getStrategy(TraderType type) {
        return switch (type) {
            case FRIENDLY -> new FriendlyTradeStrategy();
            case NEUTRAL -> new NeutralTradeStrategy();
            case GRUMPY -> new GrumpyTradeStrategy();
        };
    }
}
