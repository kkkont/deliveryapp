package com.example.deliveryapp.controller;

import com.example.deliveryapp.service.DeliveryFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {
    private final DeliveryFeeService deliveryFeeService;

    public DeliveryController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @GetMapping("/fee")
    public ResponseEntity<Double> getDeliveryFee(@RequestParam String city, @RequestParam String vehicleType) throws Exception {
        Double fee = deliveryFeeService.calculateDeliveryFee(city, vehicleType);
        return ResponseEntity.ok(fee);
    }
}