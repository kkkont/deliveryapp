package com.example.deliveryapp.repository;




import java.util.List;


import com.example.deliveryapp.entity.WeatherData;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findByStationName(String stationName);

    @Query("SELECT w FROM WeatherData w WHERE w.stationName = :stationName ORDER BY w.timestamp DESC")
    List<WeatherData> findLatestByStationName(@Param("stationName") String stationName, Pageable pageable);
}
