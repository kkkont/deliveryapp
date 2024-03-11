package com.example.deliveryapp;

import com.example.deliveryapp.controller.ApiController;
import com.example.deliveryapp.entity.Observations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan("com.example.deliveryapp.entity")
@EnableJpaRepositories("com.example.deliveryapp.repository")
public class DeliveryappApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryappApplication.class, args);
        ApiController apiController = new ApiController();
        Observations observations= apiController.getObservations();
        System.out.println(observations.getStations());
    }


}
