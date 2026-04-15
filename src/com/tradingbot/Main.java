package com.tradingbot;

import com.tradingbot.data.CsvDataLoader;
import com.tradingbot.model.Candle;
import com.tradingbot.model.Signal;
import com.tradingbot.strategy.MovingAverageCrossStrategy;
import com.tradingbot.strategy.Strategy;
import com.tradingbot.execution.PortfolioManager;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        String[] symbols = {"TCS", "INFY"};

        Strategy strategy = new MovingAverageCrossStrategy();
        PortfolioManager portfolio = new PortfolioManager(100000);

        // ✅ Load all data
        Map<String, List<Candle>> marketData = new HashMap<>();
        for (String symbol : symbols) {
            marketData.put(symbol, CsvDataLoader.load(symbol + ".csv"));
        }

        // ✅ Find minimum length
        int maxSteps = Integer.MAX_VALUE;
        for (List<Candle> candles : marketData.values()) {
            maxSteps = Math.min(maxSteps, candles.size());
        }

        // 🔥 MAIN LOOP
        for (int i = strategy.minimumBars(); i < maxSteps; i++) {

            Map<String, Double> scores = new HashMap<>();
            Map<String, Signal> signals = new HashMap<>();

            // 🔹 Loop all stocks
            for (String symbol : symbols) {

                List<Candle> candles = marketData.get(symbol);

                List<Candle> subList = candles.subList(0, i);
                double currentPrice = candles.get(i).getClose();

                // ✅ Stop loss check
                portfolio.checkStopLoss(symbol, currentPrice);

                Optional<Signal> signalOpt = strategy.evaluate(subList);

                if (signalOpt.isPresent()) {

                    Signal signal = signalOpt.get();

                    double score = ((MovingAverageCrossStrategy) strategy).score(subList);

                    scores.put(symbol, score);
                    signals.put(symbol, signal);
                }
            }

            // 🔥 RANKING (NO LAMBDA)
            List<Map.Entry<String, Double>> sorted = new ArrayList<>(scores.entrySet());

            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            int limit = Math.min(2, sorted.size());

            for (int j = 0; j < limit; j++) {

                String symbol = sorted.get(j).getKey();
                double score = sorted.get(j).getValue();
                Signal signal = signals.get(symbol);

                System.out.println("STEP " + i + " -> SELECTED: " + symbol + " score=" + score);

                portfolio.onSignal(symbol, signal);
            }
        }

        portfolio.printSummary();
    }
}