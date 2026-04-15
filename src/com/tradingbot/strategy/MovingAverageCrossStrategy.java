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

        if (prevMa50 <= prevMa200 && ma50 > ma200) {
            return Optional.of(new Signal(Signal.Action.BUY, price));
        }

        if (prevMa50 >= prevMa200 && ma50 < ma200) {
            return Optional.of(new Signal(Signal.Action.SELL, price));
        }

        return Optional.empty();
    }
    public double score(List<Candle> candles) {

        double ma50 = IndicatorService.sma(candles, 50);
        double ma200 = IndicatorService.sma(candles, 200);
        double price = candles.get(candles.size() - 1).getClose();

        // Trend strength score
        return (ma50 - ma200) / price;
    }
}