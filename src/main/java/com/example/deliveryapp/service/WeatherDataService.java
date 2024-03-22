package com.example.deliveryapp.service;


import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class WeatherDataService {
    private final WeatherDataRepository weatherDataRepository;

    public WeatherDataService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Imports weather data from the API during application startup.
     *
     * @throws Exception if an error occurs during API handling.
     */
    @PostConstruct
    public void onStartup() throws Exception {
        importWeatherData();
    }

    /**
     * Fetches weather data from the API every hour at 10 minutes past the hour (when the api is also refreshed)
     * This method retrieves weather information from the API and stores it in the database.
     * Only data from specific weather stations ("Tartu-Tõravere", "Tallinn-Harku", and "Pärnu")
     * is processed and saved.
     *
     * @throws Exception if any error occurs during API handling
     */
    @Scheduled(cron = "0 10 * * * ?")
    public void importWeatherData() throws Exception {
        String xmlFilePath = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new URL(xmlFilePath).openStream());
        document.getDocumentElement().normalize();

        NodeList stationList = document.getElementsByTagName("station");

        for (int i = 0; i < stationList.getLength(); i++) {
            Node stationNode = stationList.item(i);
            if (stationNode.getNodeType() == Node.ELEMENT_NODE) {
                Element stationElement = (Element) stationNode;
                String stationName = stationElement.getElementsByTagName("name").item(0).getTextContent();
                if (isValidStation(stationName)) {
                    WeatherData weatherData = createWeatherData(stationElement,stationName);
                    weatherDataRepository.save(weatherData);
                }
            }
        }

    }


    /**
     * Checks if station is valid
     * @param stationName station name
     * @return
     */
    private boolean isValidStation(String stationName) {
        return stationName.equals("Tallinn-Harku") || stationName.equals("Tartu-Tõravere") || stationName.equals("Pärnu");
    }

    /**
     * Splits the element into pieces for weatherData entity.
     * @param stationElement an element from xml file
     * @param stationName station name
     * @return
     */
    private WeatherData createWeatherData(Element stationElement, String stationName) {
        WeatherData weatherData = new WeatherData();
        weatherData.setStationName(stationName);
        weatherData.setWmoCode(stationElement.getElementsByTagName("wmocode").item(0).getTextContent());
        weatherData.setAirTemperature(Double.parseDouble(stationElement.getElementsByTagName("airtemperature").item(0).getTextContent()));
        weatherData.setWindSpeed(Double.parseDouble(stationElement.getElementsByTagName("windspeed").item(0).getTextContent()));
        weatherData.setWeatherPhenomenon(stationElement.getElementsByTagName("phenomenon").item(0).getTextContent());
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        weatherData.setTimestamp(now);
        return weatherData;
    }
}

