package com.tradingbot.model;

public class Position {

    private String symbol;
    private int quantity;
    private double entryPrice;

    public Position(String symbol, int quantity, double entryPrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.entryPrice = entryPrice;
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getEntryPrice() { return entryPrice; }
}