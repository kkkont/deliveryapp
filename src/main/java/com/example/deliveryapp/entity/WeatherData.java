package com.example.deliveryapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="WEATHERDATA")
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String stationName;
    private String wmocode;
    private double airtemperature;
    private double windspeed;
    private String phenomenon;
    private long timestamp;

}
