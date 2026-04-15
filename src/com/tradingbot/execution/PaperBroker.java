package com.tradingbot.execution;

import com.tradingbot.model.Signal;

public class PaperBroker {

    private double capital;
    private double riskPerTradePct = 1.0; // 1% risk

    private int quantity = 0;
    private double entryPrice = 0;

    public PaperBroker(double capital) {
        this.capital = capital;
    }

    public void execute(Signal signal) {

        double price = signal.getPrice();

        // BUY
        if (signal.getAction() == Signal.Action.BUY && quantity == 0) {

            double riskAmount = capital * (riskPerTradePct / 100);

            double stopLoss = price * 0.98; // 2% SL
            double riskPerShare = price - stopLoss;

            quantity = (int) (riskAmount / riskPerShare);

            if (quantity <= 0) return;

            entryPrice = price;

            System.out.println("BUY " + quantity + " @ " + price);
        }

        // SELL
        else if (signal.getAction() == Signal.Action.SELL && quantity > 0) {

            double pnl = (price - entryPrice) * quantity;
            capital += pnl;

            System.out.println("SELL " + quantity + " @ " + price +
                    " | PnL: " + pnl +
                    " | Capital: " + capital);

            quantity = 0;
            entryPrice = 0;
        }

    }
    public void checkStopLoss(double currentPrice) {

        if (quantity > 0) {

            double stopLoss = entryPrice * 0.98;

            if (currentPrice <= stopLoss) {

                double pnl = (currentPrice - entryPrice) * quantity;
                capital += pnl;

                System.out.println("STOP LOSS HIT @ " + currentPrice +
                        " | PnL: " + pnl +
                        " | Capital: " + capital);

                quantity = 0;
                entryPrice = 0;
            }
        }
    }
    public void printSummary() {
        System.out.println("Final Capital: " + capital);
    }
}