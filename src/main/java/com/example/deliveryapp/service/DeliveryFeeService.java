package com.example.deliveryapp.service;

import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /**
     * Calculates the delivery fee based on predefined business rules.
     *
     * @param station   The name of the station.
     * @param vehicle   The type of vehicle.
     * @param dateTime  (Optional) The specific date and time for the delivery.
     * @return The calculated delivery fee.
     * @throws Exception If there are issues with the input values (e.g., forbidden vehicle use) or if the specific date does not exist in the database.
     */
    public double calculateDeliveryFee(String station, String vehicle, LocalDateTime dateTime) throws Exception {

        String stationLC = station.toLowerCase();
        String vehicleLC = vehicle.toLowerCase();

        validateStationAndVehicle(stationLC,vehicleLC);

        String stationName = switch (stationLC) {
            case "tartu" -> "Tartu-Tõravere";
            case "tallinn" -> "Tallinn-Harku";
            case "pärnu" -> "Pärnu";
            default -> null;
        };

        WeatherData weatherData = getWeatherData(stationName,dateTime);

        double RBF = getRBF(stationLC, vehicleLC);

        double extraFee = getExtraFee(vehicleLC, weatherData);

        return RBF + extraFee;
    }

    /**
     * Checks if the input is valid or not
     * @param stationLC station name
     * @param vehicleLC vehicle type
     */
    private void validateStationAndVehicle(String stationLC, String vehicleLC) {
        if (!(stationLC.equals("tartu") || stationLC.equals("tallinn") || stationLC.equals("pärnu"))) {
            throw new IllegalArgumentException("Invalid station. Allowed values are Tartu, Tallinn, Pärnu.");
        }

        if (!(vehicleLC.equals("car") || vehicleLC.equals("bike") || vehicleLC.equals("scooter"))) {
            throw new IllegalArgumentException("Invalid vehicle. Allowed values are car, bike, scooter.");
        }
    }

    /**
     * Retrieves weather data.
     * If a date and time are specified, it determines the corresponding hour and retrieves
     * weather data for that hour or the previous hour (weather observations are refreshed
     * every 10 minutes past the full hour, so hour:09 belongs to previous observation).
     * @param stationName station name
     * @param dateTime date and time if specified
     * @return weather data
     * @throws Exception if dateTime is specified but there is no data for this specific date and time then throws exception
     */
    private WeatherData getWeatherData(String stationName, LocalDateTime dateTime) throws Exception {
        WeatherData weatherData ;
        if (dateTime != null) {
            LocalDateTime specificTime =  dateTime.withMinute(10);

            if(specificTime.isAfter(dateTime)){
                specificTime = dateTime.withMinute(10).minusHours(1);
            }

            weatherData = getWeatherDataForStationByTime(stationName, specificTime);

            if (weatherData == null) throw new Exception("There is no data for this specific date or time!");
        } else {
            weatherData = getLatestWeatherDataForStation(stationName);
        }
        return weatherData;
    }

    /**
     * Checks if we can use latest weather data for fetching weather data
     * @param stationName station name
     * @param specificTime specific time
     * @return boolean if we can use or not
     */
    private boolean isLatestWeatherDataValidForSpecificTime(String stationName, LocalDateTime specificTime) {
        return getLatestWeatherDataForStation(stationName).getTimestamp().isAfter(specificTime) && getLatestWeatherDataForStation(stationName).getTimestamp().isBefore(specificTime.withMinute(10).plusHours(1));
    }


    /**
     * Calculates extra fee
     * @param vehicle vehicle type
     * @param weatherData weather data
     * @return extra fee for vehicle and weather
     * @throws Exception handles forbidden vehicle use
     */
    private double getExtraFee(String vehicle, WeatherData weatherData) throws Exception {
        double airTemperature =  weatherData.getAirTemperature();
        double windSpeed =  weatherData.getWindSpeed();
        String phenomenon = weatherData.getWeatherPhenomenon();

        double ATEF = getATEF(vehicle, airTemperature);

        double WSEF = getWSEF(vehicle, windSpeed);

        double WPEF = getWPEF(vehicle, phenomenon);

        return ATEF + WSEF + WPEF;
    }

    /**
     * Calculates WPEF based on vehicle type and phenomenon
     * @param vehicle vehicle type
     * @param phenomenon phenomenon
     * @return WPEF
     * @throws Exception handles forbidden vehicle use
     */
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

    /**
     * Finds phenomenon class
     * @param phenomenon phenomenon
     * @return phenomenon class
     */
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

    /**
     * Calculated WSEF based on vehicle and windspeed
     * @param vehicle vehicle type
     * @param windSpeed windspeed
     * @return WSEF
     * @throws Exception handles forbidden vehicle use
     */
    private double getWSEF(String vehicle, double windSpeed) throws Exception {
        if (vehicle.equals("bike")) {
            if (windSpeed > 10 && windSpeed <= 20) return 0.5;
            else if (windSpeed > 20) throw new Exception("Usage of selected vehicle type is forbidden");
        }
        return 0;
    }

    /**
     * Calculates ATEF based on vehicle and air temperature
     * @param vehicle vehicle
     * @param airTemperature air temperature
     * @return ATEF
     */
    private double getATEF(String vehicle, double airTemperature) {
        if (vehicle.equals("scooter") || vehicle.equals("bike")) {
            if (airTemperature <= -10.0) return 1;
            else if (-10 < airTemperature && airTemperature <= 0) return 0.5;
        }
        return 0;
    }

    /**
     * Finds the RBF value for specific station and vehicle
     * @param station station
     * @param vehicle vehicle
     * @return RBF value
     */
    private double getRBF(String station, String vehicle) {
        getRBFValues();
        Map<String, Double> rbfByStation = rbfValues.get(station);
        return rbfByStation.get(vehicle);
    }

    /**
     * Map for storing RBF values for different stations and vehicles
     */
    private void getRBFValues() {
        Map<String, Double> tallinnRBF = new HashMap<>();
        tallinnRBF.put("car", 4.0);
        tallinnRBF.put("scooter", 3.5);
        tallinnRBF.put("bike", 3.0);
        rbfValues.put("tallinn", tallinnRBF);

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

    /**
     * Fetches most recent weather data for specific city
     * @param city city
     * @return weather data
     */
    private WeatherData getLatestWeatherDataForStation(String city) {
        PageRequest pageable = PageRequest.of(0, 1); // Fetch the first record
        return weatherDataRepository.findTopByStationNameOrderByTimestampDesc(city, pageable).get(0);
    }

    /**
     * Fetches weather data for specific city/station by specific date
     * @param stationName station name
     * @param dateTime date
     * @return weather data
     */
    private WeatherData getWeatherDataForStationByTime(String stationName, LocalDateTime dateTime) {
        return weatherDataRepository.findByStationNameAndTimestamp(stationName, dateTime);
    }

}
