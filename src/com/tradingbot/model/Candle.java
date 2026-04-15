package com.tradingbot.model;

public class Candle {

    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    public Candle(double open, double high, double low, double close, long volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public double getOpen() { return open; }
    public double getHigh() { return high; }
    public double getLow() { return low; }
    public double getClose() { return close; }
    public long getVolume() { return volume; }
}