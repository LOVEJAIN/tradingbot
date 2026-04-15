package com.tradingbot.strategy;

import com.tradingbot.model.Candle;
import com.tradingbot.model.Signal;

import java.util.List;
import java.util.Optional;

public interface Strategy {
    Optional<Signal> evaluate(List<Candle> candles);
    int minimumBars();
}