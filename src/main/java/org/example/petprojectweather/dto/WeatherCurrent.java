package org.example.petprojectweather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherCurrent(String time, long interval,
                             double temperature_2m, double wind_speed_10m) {
}
