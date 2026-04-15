package com.tradingbot.model;

public class Candle {

    private double open, high, low, close;
    private long volume;

    public Candle(double open, double high, double low, double close, long volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public double getClose() {
        return close;
    }
}