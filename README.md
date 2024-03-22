# Delivery Fee Calculator Application

## Overview
Sub-functionality of the food delivery application which calculates the delivery fee for food couriers based on regional base fee, vehicle type, and weather conditions.

## Technologies Used
- Java
- Spring Framework
- H2 Database

## Features
- **Database for Storing and Manipulating Data:** Uses H2 database technology to store and manage weather data fetched from an external API (https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php)
- **Configurable Scheduled Task:** Implements a scheduled task using CronJob to import weather data at regular intervals. 
- **Functionality to Calculate Delivery Fee:** Calculates the delivery fee based on city, vehicle type, and (optional) date.
- **REST Interface:** Provides a RESTful API endpoint to request the delivery fee based on input parameters.

## Setup
1. **Clone the repository:**
```
git clone https://github.com/kkkont/deliveryapp
```
2. **Configure the database connection in application.properties file:**
```
spring.datasource.url=jdbc:h2:file:<your_absolute_path_to_h2db_directory>
```
3. **Run the main application**
   - Navigate to the root directory of the project.
   - Run the main application class DeliveryappApplication.java located in the java/com/example/deliveryapp/ directory.


4. **Interact with the REST interface:**
    - Once the application is running, you can use tools like curl or Postman to interact with the RESTful API endpoints.
    - For example, you can use curl to make GET requests like:
        ```
        curl -X GET "http://localhost:8080/delivery/fee?city=tallinn&vehicleType=scooter"
        ```
    - Alternatively, you can use Postman to make HTTP requests and visualize the responses in a user-friendly interface.
      ```
      localhost:8080/delivery/fee?city=tallinn&vehicleType=car
        ```
