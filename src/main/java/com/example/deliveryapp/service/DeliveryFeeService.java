package com.example.deliveryapp.service;

import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeliveryFeeService {
    private final Map<String, Map<String, Double>> rbfValues = new HashMap<>();
    private final WeatherDataRepository weatherDataRepository;

    public DeliveryFeeService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    public double calculateDeliveryFee(String station, String vehicle) throws Exception {
        String stationLC = station.toLowerCase();
        String vehicleLC = vehicle.toLowerCase();

        if (!(stationLC.equals("tartu") || stationLC.equals("tallinn") || stationLC.equals("pärnu"))) {
            throw new IllegalArgumentException("Invalid station. Allowed values are Tartu, Tallinn, Pärnu.");
        }

        if (!(vehicleLC.equals("car") || vehicleLC.equals("bike") || vehicleLC.equals("scooter"))) {
            throw new IllegalArgumentException("Invalid vehicle. Allowed values are car, bike, scooter.");
        }

        String stationName = switch (stationLC) {
            case "tartu" -> "Tartu-Tõravere";
            case "tallinn" -> "Tallinn-Harku";
            case "pärnu" -> "Pärnu";
            default -> null;
        };
        WeatherData latestWeatherData = getLatestWeatherDataForStation(stationName);

        double RBF = getRBF(stationLC, vehicleLC);

        double extraFee = getExtraFee(vehicleLC, latestWeatherData);

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
        if (vehicle.equals("scooter") || vehicle.equals("bike")) {
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
        if (vehicle.equals("bike")) {
            if (windSpeed > 10 && windSpeed <= 20) return 0.5;
            else if (windSpeed > 20) throw new Exception("Usage of selected vehicle type is forbidden");
        }
        return 0;
    }

    private double getATEF(String vehicle, double airTemperature) {
        if (vehicle.equals("scooter") || vehicle.equals("bike")) {
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
        tallinRBF.put("car", 4.0);
        tallinRBF.put("scooter", 3.5);
        tallinRBF.put("bike", 3.0);
        rbfValues.put("tallinn", tallinRBF);

        Map<String, Double> tartuRBF = new HashMap<>();
        tartuRBF.put("car", 3.5);
        tartuRBF.put("scooter", 3.0);
        tartuRBF.put("bike", 2.5);
        rbfValues.put("tartu", tartuRBF);

        Map<String, Double> parnuRBF = new HashMap<>();
        parnuRBF.put("car", 3.0);
        parnuRBF.put("scooter", 2.5);
        parnuRBF.put("bike", 2.0);
        rbfValues.put("pärnu", parnuRBF);
    }

    private WeatherData getLatestWeatherDataForStation(String city) {
        PageRequest pageable = PageRequest.of(0, 1); // Fetch the first record
        return weatherDataRepository.findTopByStationNameOrderByTimestampDesc(city, pageable).get(0);
    }

}
