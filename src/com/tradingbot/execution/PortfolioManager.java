package com.tradingbot.execution;

import com.tradingbot.model.Position;
import com.tradingbot.model.Signal;
import com.tradingbot.strategy.IndicatorService;
import com.tradingbot.model.Candle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortfolioManager {
    private int totalTrades = 0;
    private int winningTrades = 0;
    private int losingTrades = 0;
    private double capital;
    private double riskPct = 1.0;

    private Map<String, Position> positions = new HashMap<>();
    private int maxPositions = 3;

    private Map<String, Integer> cooldownMap = new HashMap<>();
    private int cooldownPeriod = 10;

    public PortfolioManager(double capital) {
        this.capital = capital;
    }

    public void onSignal(String symbol, Signal signal, List<Candle> candles) {

        double price = signal.getPrice();

        // ❌ Cooldown check
        if (cooldownMap.getOrDefault(symbol, 0) > 0) return;

        // ❌ Already holding
        if (positions.containsKey(symbol)) return;

        // ✅ BUY
        if (signal.getAction() == Signal.Action.BUY && positions.size() < maxPositions) {

            double atr = IndicatorService.atr(candles, 14);
            if (atr == 0) return;

            double stopLoss = price - (2 * atr);
            double riskPerShare = price - stopLoss;

            double riskAmount = capital * (riskPct / 100);
            int qty = (int) (riskAmount / riskPerShare);

            if (qty <= 0) return;

            positions.put(symbol, new Position(symbol, qty, price, stopLoss));

            System.out.println(symbol + " BUY " + qty + " @ " + price +
                    " | SL=" + stopLoss);
        }

        // ✅ SELL
        if (signal.getAction() == Signal.Action.SELL && positions.containsKey(symbol)) {

            Position pos = positions.get(symbol);

            double pnl = (price - pos.getEntryPrice()) * pos.getQuantity();
            capital += pnl;

            totalTrades++;

            if (pnl > 0) winningTrades++;
            else losingTrades++;

            System.out.println(symbol + " SELL @ " + price +
                    " | PnL: " + pnl +
                    " | Capital: " + capital);

            positions.remove(symbol);
        }
    }

    public void checkStopLoss(String symbol, double currentPrice) {

        if (!positions.containsKey(symbol)) return;

        Position pos = positions.get(symbol);

        // 🔥 Update highest price
        if (currentPrice > pos.getHighestPrice()) {
            pos.setHighestPrice(currentPrice);

            // 🔥 TRAILING STOP (lock profit)
            double newSL = pos.getHighestPrice() - (pos.getEntryPrice() * 0.02);

            if (newSL > pos.getStopLoss()) {
                pos.setStopLoss(newSL);
            }
        }

        // 🔥 PARTIAL PROFIT BOOKING (2R move)
        double risk = pos.getEntryPrice() - pos.getStopLoss();

        if (!pos.isPartialBooked() && currentPrice >= pos.getEntryPrice() + (2 * risk)) {

            int qtyToSell = pos.getQuantity() / 2;

            double pnl = (currentPrice - pos.getEntryPrice()) * pos.getQuantity();
            capital += pnl;

            totalTrades++;

            if (pnl > 0) winningTrades++;
            else losingTrades++;

            pos.setPartialBooked(true);
            pos.setQuantity(pos.getQuantity() - qtyToSell);

            System.out.println(symbol + " PARTIAL BOOK @ " + currentPrice +
                    " | PnL: " + pnl +
                    " | Remaining Qty: " + pos.getQuantity());
        }

        // 🔥 FINAL STOP LOSS
        if (currentPrice <= pos.getStopLoss()) {

            double pnl = (currentPrice - pos.getEntryPrice()) * pos.getQuantity();
            capital += pnl;

            System.out.println(symbol + " EXIT @ " + currentPrice +
                    " | PnL: " + pnl +
                    " | Capital: " + capital);

            positions.remove(symbol);

            cooldownMap.put(symbol, cooldownPeriod);
        }
    }

    public void updateCooldowns() {

        Map<String, Integer> newMap = new HashMap<>();

        for (String symbol : cooldownMap.keySet()) {
            int val = cooldownMap.get(symbol) - 1;
            if (val > 0) {
                newMap.put(symbol, val);
            }
        }

        cooldownMap = newMap;
    }

    public void printSummary() {
        System.out.println("Final Capital: " + capital);
        System.out.println("Total Trades: " + totalTrades);
        System.out.println("Winning Trades: " + winningTrades);
        System.out.println("Losing Trades: " + losingTrades);

        if (totalTrades > 0) {
            double winRate = (winningTrades * 100.0) / totalTrades;
            System.out.println("Win Rate: " + winRate + "%");
        }
    }
}