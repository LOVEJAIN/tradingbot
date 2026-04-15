package com.tradingbot.model;

public class Signal {

    public enum Action {
        BUY, SELL
    }

    private Action action;
    private double price;

    public Signal(Action action, double price) {
        this.action = action;
        this.price = price;
    }

    public Action getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }
}