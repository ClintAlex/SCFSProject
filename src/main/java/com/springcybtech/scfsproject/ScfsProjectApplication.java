package com.springcybtech.scfsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScfsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScfsProjectApplication.class, args);
    }

}
