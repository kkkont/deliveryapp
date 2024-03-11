package com.example.deliveryapp.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long observationId;
    private String stationName;
    private double airtemperature;
    private double windspeed;
    private String phenomenon;
    private long timestamp;

}
