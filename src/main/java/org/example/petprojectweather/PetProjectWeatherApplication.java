package org.example.petprojectweather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PetProjectWeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetProjectWeatherApplication.class, args);
    }

}
