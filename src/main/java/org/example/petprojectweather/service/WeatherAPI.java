package org.example.petprojectweather.service;

import org.example.petprojectweather.dto.WeatherCity;

public interface WeatherAPI {
    WeatherCity getWeatherAroundCity(String city);

}
