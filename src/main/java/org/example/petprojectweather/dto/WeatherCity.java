package org.example.petprojectweather.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public record WeatherCity(String city,
                          WeatherResponseAPIOpenMeteo weatherResponseAPIOpenMeteo)implements Serializable {
}
