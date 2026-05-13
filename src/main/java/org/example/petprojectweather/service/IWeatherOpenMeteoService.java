package org.example.petprojectweather.service;

import org.example.petprojectweather.Utils;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.dto.WeatherLog;
import org.example.petprojectweather.dto.WeatherResponseAPIOpenMeteo;
import org.example.petprojectweather.entity.City;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class IWeatherOpenMeteoService implements IWeatherAPI {
    private final RestClient restClient;
    private final CityService cityService;
    private final WeatherLogService weatherLogService;

    public IWeatherOpenMeteoService(CityService cityService, WeatherLogService weatherLogService) {
        this.weatherLogService = weatherLogService;
        this.restClient = RestClient.builder().build();
        this.cityService=cityService;
    }


    @Override
    public WeatherCity getWeatherAroundCity(String cityName) {
        String normalized= cityName.toLowerCase().strip();
        City city = cityService.getCityByCityName(normalized);
        WeatherResponseAPIOpenMeteo weatherResponse =
                getWeather(Map.of("lat", String.valueOf(city.getLatitude()),
                        "lon", String.valueOf(city.getLongitude())));
        weatherLogService.saveLog(new WeatherLog(LocalDateTime.now(),new WeatherCity(normalized,weatherResponse)));
        return new WeatherCity(normalized, weatherResponse);
    }

    private WeatherResponseAPIOpenMeteo getWeather(Map<String, String> uriVariable) {
        return restClient.get()
                .uri(Utils.URL, uriVariable)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Ошибка API: " + response.getStatusCode());
                })
                .body(WeatherResponseAPIOpenMeteo.class);
    }
}
