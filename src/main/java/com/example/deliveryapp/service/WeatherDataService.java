package com.example.deliveryapp.service;


import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;

@Service
public class WeatherDataService {
    @Autowired
    private WeatherDataRepository weatherDataRepository;

    //@Scheduled(cron = "0 15 * * * ?")
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
                String station_name = stationElement.getElementsByTagName("name").item(0).getTextContent();
                System.out.println("SIIN!!!");
                if (station_name.equals("Tallinn-Harku") || station_name.equals("Tartu-Tõravere") || station_name.equals("Pärnu")) {
                    WeatherData weatherData = new WeatherData();
                    weatherData.setStationName(station_name);
                    weatherData.setWmoCode(stationElement.getElementsByTagName("wmocode").item(0).getTextContent());
                    weatherData.setAirTemperature(Double.parseDouble(stationElement.getElementsByTagName("airtemperature").item(0).getTextContent()));
                    weatherData.setWindSpeed(Double.parseDouble(stationElement.getElementsByTagName("windspeed").item(0).getTextContent()));
                    weatherData.setWeatherPhenomenon(stationElement.getElementsByTagName("phenomenon").item(0).getTextContent());
                    weatherData.setTimestamp(LocalDateTime.now());

                    weatherDataRepository.save(weatherData);
                }
            }
        }

    }


}

