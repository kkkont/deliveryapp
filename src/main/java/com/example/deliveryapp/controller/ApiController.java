package com.example.deliveryapp.controller;

import com.example.deliveryapp.entity.Observations;
import com.example.deliveryapp.entity.Station;
import com.example.deliveryapp.entity.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiController {

    public Observations getObservations() {
        String uri = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        RestTemplate restTemplate = new RestTemplate();
        String xmlResponse = restTemplate.getForObject(uri, String.class);

        try {
            JAXBContext context = JAXBContext.newInstance(Observations.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlResponse);
            Observations observations = (Observations) unmarshaller.unmarshal(reader);

            List<Station> filteredStations = new ArrayList<>();

            for (Station station : observations.getStations()) {
                String stationName = station.getName();
                if ("Tallinn-Harku".equals(stationName) || "Tartu-Tõravere".equals(stationName) || "Pärnu".equals(stationName)) {
                    filteredStations.add(station);
                }
            }

            // Update the Observations object with the filtered stations
            Observations filteredObservations = new Observations();
            filteredObservations.setStations(filteredStations);

            return filteredObservations;
        } catch (JAXBException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
    }



}
