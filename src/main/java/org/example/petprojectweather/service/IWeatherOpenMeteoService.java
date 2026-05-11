package org.example.petprojectweather.service;

import org.example.petprojectweather.Utils;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.dto.WeatherResponseAPIOpenMeteo;
import org.example.petprojectweather.entity.City;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class IWeatherOpenMeteoService implements IWeatherAPI {
    private final RestClient restClient;
    private final CityService cityService;

    public IWeatherOpenMeteoService(CityService cityService) {
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
