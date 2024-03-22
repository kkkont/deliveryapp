package com.example.deliveryapp.controller;

import com.example.deliveryapp.service.DeliveryFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryFeeService deliveryFeeService;

    public DeliveryController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Retrieves the delivery fee based on the specified city and vehicle type,
     * optionally considering the provided date and time.
     *
     * @param city        The city for the delivery.
     * @param vehicleType The type of vehicle used for the delivery.
     * @param dateTime    (Optional) The date and time of the delivery.
     * @return The delivery fee or an error message if invalid parameters are provided.
     */
    @GetMapping("/fee")
    public ResponseEntity<?> getDeliveryFee(@RequestParam String city, @RequestParam String vehicleType, @RequestParam(required = false) String dateTime) {
        try {
            LocalDateTime parsedDateTime = null;
            if (dateTime != null) {
                parsedDateTime = LocalDateTime.parse(dateTime);
            }
            Double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType, parsedDateTime);
            return ResponseEntity.ok(fee);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date time format. Please provide date time in 'yyyy-MM-dd'T'HH:mm:ss' format.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}