package org.example.petprojectweather.dto;

public record WeatherCity(String city,
                          WeatherResponseAPIOpenMeteo weatherResponseAPIOpenMeteo) {
}
