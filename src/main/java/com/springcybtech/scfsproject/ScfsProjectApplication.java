package com.springcybtech.scfsproject;

import com.springcybtech.scfsproject.services.impl.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScfsProjectApplication implements CommandLineRunner {

    @Autowired
    private WeatherServiceImpl weatherService;

    public static void main(String[] args) {
        SpringApplication.run(ScfsProjectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Call the fetchAndSaveWeather method with a sample city
        //weatherService.fetchAndSaveWeather("Odense");
        // Print a message to indicate the method has been called
        //System.out.println("Weather data fetched and saved for Odense.");
    }
}
