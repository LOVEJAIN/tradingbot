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

// ✅ Relaxed threshold
        if (trendStrength < 0.005) {
            return Optional.empty();
        }

        double atr = IndicatorService.atr(candles, 14);

// ✅ Relaxed threshold
        if (atr == 0 || (atr / price) < 0.005) {
            return Optional.empty();
        }
        System.out.println("Checking candle " + n +
                " trend=" + trendStrength +
                " atr=" + (atr/price));
        // ==============================
        // 🔥 3. SLOPE FILTER
        // ==============================
        boolean uptrend = ma50 > prevMa50;
        boolean downtrend = ma50 < prevMa50;

        // ==============================
        // 🔥 4. SIGNAL LOGIC
        // ==============================
        // ==============================
// 🔥 HIGHER TIMEFRAME FILTER
// ==============================
        boolean higherTimeframeUp = ma200 > prevMa200;
        boolean higherTimeframeDown = ma200 < prevMa200;
        // BUY only if higher timeframe supports
        if (prevMa50 <= prevMa200 && ma50 > ma200 && uptrend && higherTimeframeUp) {
            return Optional.of(new Signal(Signal.Action.BUY, price));
        }

// SELL only if higher timeframe supports
        if (prevMa50 >= prevMa200 && ma50 < ma200 && downtrend && higherTimeframeDown) {
            return Optional.of(new Signal(Signal.Action.SELL, price));
        }

        return Optional.empty();
    }

    // ==============================
    // 🔥 SCORING FUNCTION (for ranking)
    // ==============================
    public double score(List<Candle> candles) {

        double ma50 = IndicatorService.sma(candles, 50);
        double ma200 = IndicatorService.sma(candles, 200);
        double price = candles.get(candles.size() - 1).getClose();

        return (ma50 - ma200) / price;
    }
}