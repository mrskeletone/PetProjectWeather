package org.example.petprojectweather.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CacheWeather(double temperature, double windSpeed, LocalDateTime timestamp) implements Serializable {
}
