package com.tradingbot.model;

public class Position {

    private String symbol;
    private int quantity;
    private double entryPrice;
    private double stopLoss;
    private double highestPrice;
    private boolean partialBooked;

    public Position(String symbol, int quantity, double entryPrice, double stopLoss) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
        this.stopLoss = stopLoss;
        this.highestPrice = entryPrice;
        this.partialBooked = false;
    }

    public double getStopLoss() { return stopLoss; }
    public Position(String symbol, int quantity, double entryPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
    }
    public double getHighestPrice() { return highestPrice; }
    public void setHighestPrice(double highestPrice) { this.highestPrice = highestPrice; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public boolean isPartialBooked() { return partialBooked; }
    public void setPartialBooked(boolean partialBooked) { this.partialBooked = partialBooked; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getEntryPrice() { return entryPrice; }
}