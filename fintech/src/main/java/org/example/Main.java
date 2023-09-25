package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    private static String[] regions = {
            "Moscow",
            "Saint Petersburg",
            "Kazan",
    };

    public static ArrayList<Weather> generateWeatherList() {
        Random random = new Random();

        ArrayList<Weather> weathers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < regions.length; j++) {
                weathers.add(new Weather(
                        (long) (j + 1),
                        regions[j],
                        random.nextDouble(-30, 30),
                        LocalDateTime.now()
                ));
            }
        }

        return weathers;
    }

    public static Map<String, Double> getAvgTempInRegions(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionName,
                        Collectors.averagingDouble(Weather::getTemperature)));
    }

    public static List<String> filterRegionsTempHigh(List<Weather> weathers, int temp) {
        return weathers.stream()
                .filter(weather -> weather.getTemperature() > temp)
                .map(region -> region.getRegionName())
                .distinct()
                .toList();
    }

    public static Map<Long, List<Double>> getTempsInRegions(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getId,
                        Collectors.mapping(Weather::getTemperature, Collectors.toList())));
    }

    public static Map<Integer, List<Weather>> getRegionsGroupedByTemp(List<Weather> weathers) {
        Function<Weather, Double> getTemp = Weather::getTemperature;
        Function<Weather, Integer> getIntTemp = getTemp.andThen(Double::intValue);

        return weathers.stream()
                .collect(Collectors.groupingBy(getIntTemp));
    }

    public static void main(String[] args) {
        ArrayList<Weather> weathers = generateWeatherList();

        System.out.println("List of Weather objects:");
        for (Weather weather : weathers) {
            System.out.println("\t" + weather.toString());
        }

        Map<String, Double> avgTempInRegions = getAvgTempInRegions(weathers);

        System.out.println("Average temperature in regions:");
        for (Entry<String, Double> entry : avgTempInRegions.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }

        int tempForFilter = 10;
        List<String> regionsTempHigh = filterRegionsTempHigh(weathers, tempForFilter);

        System.out.println("Regions where the temperature is higher than " + tempForFilter + ":");
        for (String region : regionsTempHigh) {
            System.out.println("\t" + region.toString());
        }

        Map<Long, List<Double>> tempsInRegions = getTempsInRegions(weathers);

        System.out.println("Lists of temperatures by region");
        for (Entry<Long, List<Double>> entry : tempsInRegions.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }

        Map<Integer, List<Weather>> regionsGroupedByTemp = getRegionsGroupedByTemp(weathers);

        System.out.println("Lists of regions by temperature");
        for (Entry<Integer, List<Weather>> entry : regionsGroupedByTemp.entrySet()) {
            System.out.println("\t" + entry.getKey() + ": " + entry.getValue());
        }
    }
}