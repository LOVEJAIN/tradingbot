package com.tradingbot.strategy;

import com.tradingbot.model.Candle;

import java.util.List;

public class IndicatorService {

    public static double atr(List<Candle> candles, int period) {

        int n = candles.size();
        if (n < period + 1) return 0;

        double sum = 0;

        for (int i = n - period; i < n; i++) {

            double high = candles.get(i).getHigh();
            double low = candles.get(i).getLow();
            double prevClose = candles.get(i - 1).getClose();

            double tr = Math.max(high - low,
                    Math.max(Math.abs(high - prevClose), Math.abs(low - prevClose)));

            sum += tr;
        }

        return sum / period;
    }
    public static double sma(List<Candle> candles, int period) {

        int n = candles.size();
        if (n < period) return 0;

        double sum = 0;

        for (int i = n - period; i < n; i++) {
            sum += candles.get(i).getClose();
        }

        return sum / period;
    }
}