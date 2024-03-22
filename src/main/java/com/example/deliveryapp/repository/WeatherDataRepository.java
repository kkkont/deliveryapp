package com.example.deliveryapp.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.deliveryapp.entity.WeatherData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findTopByStationNameOrderByTimestampDesc(String stationName, Pageable pageable);
    WeatherData findByStationNameAndTimestamp(String stationName, LocalDateTime timestamp);

}
