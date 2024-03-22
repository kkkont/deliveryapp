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
     * imports weather data on application startup
     * @throws Exception anything gone wrong on api handling
     */
    @PostConstruct
    public void onStartup() throws Exception {
        importWeatherData();
    }

    /**
     * On the 10nth minute of every hour method fetches weather data from api.
     * Method sorts out specific station which are required for
     * this application ("Tartu-Tõravere", "Tallinn-Harku" and "Pärnu")
     * Method also saves weather data to database
     * @throws Exception anything gone wrong on api handling
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

