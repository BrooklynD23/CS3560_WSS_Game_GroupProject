package wss.economy;

public class NeutralTradeStrategy implements TradeStrategy 
{
    @Override
    public Trader.TradeResult evaluateOffer(int goldOffered, int foodOffered, int waterOffered, int currentPatience) {
        int total = goldOffered + foodOffered + waterOffered;
        if (total >= 2) {
            return Trader.TradeResult.ACCEPT;
        }
        if (currentPatience <= 1) {
            return Trader.TradeResult.REJECT;
        }
        return Trader.TradeResult.COUNTER;
    }
    
    @Override
    public int getInitialPatience() {
        return 2;
    }
    
    @Override
    public String getRewardResource() {
        return "food";
    }
    
    @Override
    public int getRewardPerGold() {
        return 2;
    }
}