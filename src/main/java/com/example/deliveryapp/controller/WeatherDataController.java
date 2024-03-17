package com.example.deliveryapp.controller;

import java.util.ArrayList;
import java.util.List;
// Importing required classes

import com.example.deliveryapp.entity.WeatherData;
import com.example.deliveryapp.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Annotation
@RestController

// Class
public class WeatherDataController {
    @Autowired
    private WeatherDataRepository weatherDataRepository;


    // Save operation
    @PostMapping("/addWeatherData")
    public ResponseEntity<WeatherData> saveWeatherData( @RequestBody WeatherData weatherData) {
        WeatherData obj = weatherDataRepository.save(weatherData);
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    // Read operation
    @GetMapping
    public ResponseEntity<List<WeatherData>> fetchWeatherList() {
        try{
            List<WeatherData> list = new ArrayList<>();
            weatherDataRepository.findAll().forEach(list::add);
            if(list.isEmpty()) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(list, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}