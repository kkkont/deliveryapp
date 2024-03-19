package com.example.deliveryapp.service;

import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeliveryFeeService {
    @Autowired
    private WeatherDataRepository weatherDataRepository;

    private final Map<String, Map<String, Double>> rbfValues = new HashMap<>();

    public double calculateDeliveryFee(String station, String vehicle) throws Exception {
        double RBF = getRBF(station, vehicle);

        WeatherData latestWeatherData = getLatestWeatherDataForStation(station);
        double extraFee = getExtraFee(vehicle, latestWeatherData);

        return RBF + extraFee;
    }

    private double getExtraFee(String vehicle, WeatherData latestWeatherData) throws Exception {
        double airTemperature = latestWeatherData.getAirTemperature();
        double windSpeed = latestWeatherData.getWindSpeed();
        String phenomenon = latestWeatherData.getWeatherPhenomenon();

        double ATEF = getATEF(vehicle, airTemperature);

        double WSEF = getWSEF(vehicle, windSpeed);

        double WPEF = getWPEF(vehicle, phenomenon);

        return ATEF + WSEF + WPEF;
    }

    private double getWPEF(String vehicle, String phenomenon) throws Exception {
        if (vehicle.equals("Scooter") || vehicle.equals("Bike")) {
            String phenomenonClass = getPhenomenonClass(phenomenon);

            switch (phenomenonClass) {
                case "Snow" -> {
                    return 1;
                }
                case "Rain" -> {
                    return 0.5;
                }
                case "Glaze" -> throw new Exception("Usage of selected vehicle type is forbidden");
            }
        }
        return 0;
    }

    private String getPhenomenonClass(String phenomenon) {
        String[] phenomenonTypeSnow = {"Light snow shower", "Moderate snow shower", "Heavy snow shower", "Light snowfall", "Moderate snowfall", "Heavy snowfall", "Blowing snow", "Drifting snow"};
        String[] phenomenonTypeRain = {"Light shower", "Moderate shower", "Heavy shower", "Light rain", "Moderate rain", "Heavy rain"};
        String[] phenomenonTypeGlaze = {"Glaze", "Light sleet", "Moderate sleet", "Hail", "Thunder", "Thunderstorm"};

        boolean isSnow = Arrays.asList(phenomenonTypeSnow).contains(phenomenon);
        boolean isRain = Arrays.asList(phenomenonTypeRain).contains(phenomenon);
        boolean isGlaze = Arrays.asList(phenomenonTypeGlaze).contains(phenomenon);

        if (isSnow) return "Snow";
        else if (isRain) return "Rain";
        else if (isGlaze) return "Glaze";
        else return "Other";
    }

    private double getWSEF(String vehicle, double windSpeed) throws Exception {
        if (vehicle.equals("Bike")) {
            if (windSpeed > 10 && windSpeed <= 20) return 0.5;
            else if (windSpeed > 20) throw new Exception("Usage of selected vehicle type is forbidden");
        }
        return 0;
    }

    private double getATEF(String vehicle, double airTemperature) {
        if (vehicle.equals("Scooter") || vehicle.equals("Bike")) {
            if (airTemperature <= -10.0) return 1;
            else if (-10 < airTemperature && airTemperature <= 0) return 0.5;
        }
        return 0;
    }

    private double getRBF(String station, String vehicle) {
        getRBFValues();
        Map<String, Double> rbfByStation = rbfValues.get(station);
        return rbfByStation.get(vehicle);
    }

    private void getRBFValues() {
        Map<String, Double> tallinRBF = new HashMap<>();
        tallinRBF.put("Car", 4.0);
        tallinRBF.put("Scooter", 3.5);
        tallinRBF.put("Bike", 3.0);
        rbfValues.put("Tallinn-Harku", tallinRBF);

        Map<String, Double> tartuRBF = new HashMap<>();
        tartuRBF.put("Car", 3.5);
        tartuRBF.put("Scooter", 3.0);
        tartuRBF.put("Bike", 2.5);
        rbfValues.put("Tartu-Tõravere", tartuRBF);

        Map<String, Double> parnuRBF = new HashMap<>();
        parnuRBF.put("Car", 3.0);
        parnuRBF.put("Scooter", 2.5);
        parnuRBF.put("Bike", 2.0);
        rbfValues.put("Pärnu", parnuRBF);
    }

    public WeatherData getLatestWeatherDataForStation(String city) {
        PageRequest pageable = PageRequest.of(0, 1); // Fetch the first record
        return weatherDataRepository.findLatestByStationName(city, pageable).get(0);
    }

}
