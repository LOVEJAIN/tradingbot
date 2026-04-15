package com.tradingbot.strategy;

import com.tradingbot.model.Candle;
import com.tradingbot.model.Signal;

import java.util.List;
import java.util.Optional;

public class MovingAverageCrossStrategy implements Strategy {

    @Override
    public int minimumBars() {
        return 200;
    }

    @Override
    public Optional<Signal> evaluate(List<Candle> candles) {

        int n = candles.size();
        if (n < 200) return Optional.empty();

        double ma50 = IndicatorService.sma(candles, 50);
        double ma200 = IndicatorService.sma(candles, 200);

        double prevMa50 = IndicatorService.sma(candles.subList(0, n - 1), 50);
        double prevMa200 = IndicatorService.sma(candles.subList(0, n - 1), 200);

        double price = candles.get(n - 1).getClose();

        // ==============================
        // 🔥 1. TREND STRENGTH FILTER
        // ==============================
        double trendStrength = Math.abs(ma50 - ma200) / price;

        if (trendStrength < 0.003) {
            // DEBUG
            System.out.println("SKIP (weak trend) @ candle " + n + " trend=" + trendStrength);
            return Optional.empty();
        }

        // ==============================
        // 🔥 2. VOLATILITY FILTER
        // ==============================
        double atr = IndicatorService.atr(candles, 14);

        if (atr == 0 || (atr / price) < 0.002) {
            // DEBUG
            System.out.println("SKIP (low volatility) @ candle " + n + " atr=" + (atr / price));
            return Optional.empty();
        }

        // ==============================
        // 🔥 3. SLOPE FILTER
        // ==============================
        boolean uptrend = ma50 > prevMa50;
        boolean downtrend = ma50 < prevMa50;

        // ==============================
        // 🔥 4. HIGHER TIMEFRAME CONFIRMATION
        // ==============================
        boolean higherTimeframeUp = ma200 > prevMa200;
        boolean higherTimeframeDown = ma200 < prevMa200;

        // ==============================
        // 🔥 5. ENTRY LOGIC (FIXED)
        // ==============================

        // ✅ TREND CONTINUATION ENTRY (NOT JUST CROSSOVER)
        if (ma50 > ma200 && uptrend && higherTimeframeUp) {
            System.out.println("BUY SIGNAL @ candle " + n +
                    " trend=" + trendStrength +
                    " atr=" + (atr / price));
            return Optional.of(new Signal(Signal.Action.BUY, price));
        }

        if (ma50 < ma200 && downtrend && higherTimeframeDown) {
            System.out.println("SELL SIGNAL @ candle " + n +
                    " trend=" + trendStrength +
                    " atr=" + (atr / price));
            return Optional.of(new Signal(Signal.Action.SELL, price));
        }

        // DEBUG
        System.out.println("SKIP (no alignment) @ candle " + n);

        return Optional.empty();
    }

    // ==============================
    // 🔥 SCORING FUNCTION
    // ==============================
    public double score(List<Candle> candles) {

        double ma50 = IndicatorService.sma(candles, 50);
        double ma200 = IndicatorService.sma(candles, 200);
        double price = candles.get(candles.size() - 1).getClose();

        return (ma50 - ma200) / price;
    }
}