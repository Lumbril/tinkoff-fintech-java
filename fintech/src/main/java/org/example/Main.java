package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static String[] regions = {
            "Moscow",
            "Saint Petersburg",
            "Kazan",
    };

    private static ArrayList<Weather> generateWeatherList() {
        Random random = new Random();

        ArrayList<Weather> weathers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < regions.length; j++) {
                weathers.add(new Weather(
                        (long) (j + 1),
                        regions[j],
                        random.nextInt(-30, 30),
                        LocalDateTime.now()
                ));
            }
        }

        return weathers;
    }

    public static void main(String[] args) {
        ArrayList<Weather> weathers = generateWeatherList();

        for (Weather weather : weathers) {
            System.out.println(weather.toString());
        }
    }
}