package com.example.deliveryapp;

import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeliveryControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetDeliveryFee() {
        ResponseEntity<Double> response = restTemplate.getForEntity("/delivery/fee?city=tallinn&vehicleType=scooter", Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Double responseBody = response.getBody();
        System.out.println("Response Body: " + responseBody);

    }
}
