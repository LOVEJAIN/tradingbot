package com.tradingbot.data;

import com.tradingbot.model.Candle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader {

    public static List<Candle> load(String filePath) {

        List<Candle> candles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                // Skip header or bad rows
                if (parts.length < 7 || parts[0].equalsIgnoreCase("tradingsymbol")) {
                    continue;
                }

                try {
                    double open = Double.parseDouble(parts[2]);
                    double high = Double.parseDouble(parts[3]);
                    double low = Double.parseDouble(parts[4]);
                    double close = Double.parseDouble(parts[5]);
                    long volume = Long.parseLong(parts[6]);

                    candles.add(new Candle(open, high, low, close, volume));

                } catch (Exception e) {
                    System.out.println("Skipping row: " + line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return candles;
    }
}