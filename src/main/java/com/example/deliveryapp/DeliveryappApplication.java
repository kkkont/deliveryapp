package com.example.deliveryapp;

import com.example.deliveryapp.service.DeliveryFeeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;


@SpringBootApplication
@EnableScheduling
public class DeliveryappApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DeliveryappApplication.class, args);

    }

}
