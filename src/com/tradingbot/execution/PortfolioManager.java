package com.tradingbot.execution;

import com.tradingbot.model.Position;
import com.tradingbot.model.Signal;

import java.util.HashMap;
import java.util.Map;

public class PortfolioManager {

    private double capital;
    private double riskPct = 1.0;

    private Map<String, Position> positions = new HashMap<>();
    private int maxPositions = 3;

    public PortfolioManager(double capital) {
        this.capital = capital;
    }

    public void onSignal(String symbol, Signal signal) {

        double price = signal.getPrice();

        // If already holding → ignore BUY
        if (positions.containsKey(symbol) && signal.getAction() == Signal.Action.BUY) {
            return;
        }

        // BUY
        if (signal.getAction() == Signal.Action.BUY && positions.size() < maxPositions) {

            double riskAmount = capital * (riskPct / 100);
            double stopLoss = price * 0.98;
            double riskPerShare = price - stopLoss;

            int qty = (int) (riskAmount / riskPerShare);
            if (qty <= 0) return;

            positions.put(symbol, new Position(symbol, qty, price));

            System.out.println(symbol + " BUY " + qty + " @ " + price);
        }

        // SELL
        if (signal.getAction() == Signal.Action.SELL && positions.containsKey(symbol)) {

            Position pos = positions.get(symbol);

            double pnl = (price - pos.getEntryPrice()) * pos.getQuantity();
            capital += pnl;

            System.out.println(symbol + " SELL @ " + price +
                    " | PnL: " + pnl +
                    " | Capital: " + capital);

            positions.remove(symbol);
        }
    }

    public void checkStopLoss(String symbol, double currentPrice) {

        if (!positions.containsKey(symbol)) return;

        Position pos = positions.get(symbol);
        double stopLoss = pos.getEntryPrice() * 0.98;

        if (currentPrice <= stopLoss) {

            double pnl = (currentPrice - pos.getEntryPrice()) * pos.getQuantity();
            capital += pnl;

            System.out.println(symbol + " SL HIT @ " + currentPrice +
                    " | PnL: " + pnl +
                    " | Capital: " + capital);

            positions.remove(symbol);
        }
    }

    public void printSummary() {
        System.out.println("Final Capital: " + capital);
    }
}