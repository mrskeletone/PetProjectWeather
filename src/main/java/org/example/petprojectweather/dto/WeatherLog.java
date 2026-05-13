package org.example.petprojectweather.dto;

import java.time.LocalDateTime;

public record WeatherLog(LocalDateTime timestamp, WeatherCity weatherCity) {
}
