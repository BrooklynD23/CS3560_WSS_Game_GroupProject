package wss.economy;

public interface TradeStrategy
{
    Trader.TradeResult evaluateOffer(int goldOffered, int foodOffered, int waterOffered, int currentPatience);
    int getInitialPatience();
    String getRewardResource();
    int getRewardPerGold();
}