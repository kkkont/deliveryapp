package com.example.deliveryapp.repository;

import com.example.deliveryapp.entity.WeatherData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface WeatherDataRepository
        extends CrudRepository<WeatherData, Long> {
}
