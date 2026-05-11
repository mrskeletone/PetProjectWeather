package org.example.petprojectweather.service;

import org.example.petprojectweather.dto.WeatherCity;

public interface IWeatherAPI {
    WeatherCity getWeatherAroundCity(String city);

}
