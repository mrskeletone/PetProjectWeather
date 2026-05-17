package org.example.petprojectweather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponseAPIOpenMeteo(double latitude, double longitude,
                                          WeatherCurrent current) implements Serializable {
}
