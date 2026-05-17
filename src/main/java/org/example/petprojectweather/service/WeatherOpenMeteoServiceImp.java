package org.example.petprojectweather.service;

import org.example.petprojectweather.Utils;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.dto.WeatherLog;
import org.example.petprojectweather.dto.WeatherResponseAPIOpenMeteo;
import org.example.petprojectweather.entity.City;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class WeatherOpenMeteoServiceImp implements WeatherAPI {
    private final RestClient restClient;
    private final CityService cityService;
    private final WeatherLogService weatherLogService;
    private final RedisTemplate<String, WeatherCity> redisTemplate;
    private static final Duration TTL=Duration.ofMinutes(5);
    private static final String CACHE_KEY_PREFIX="city:";

    public WeatherOpenMeteoServiceImp(CityService cityService, WeatherLogService weatherLogService, RedisTemplate<String, WeatherCity> redisTemplate) {
        this.weatherLogService = weatherLogService;
        this.redisTemplate = redisTemplate;
        this.restClient = RestClient.builder().build();
        this.cityService=cityService;
    }


    @Override
    public WeatherCity getWeatherAroundCity(String cityName) {
        String normalized= cityName.toLowerCase().strip();
        City city = cityService.getCityByCityName(normalized);
        String cacheKey=CACHE_KEY_PREFIX+city.getCityName();
        WeatherCity weatherCity= redisTemplate.opsForValue().get(cacheKey);
        if(weatherCity!=null){
            return weatherCity;
        }
        WeatherResponseAPIOpenMeteo weatherResponse =
                getWeather(Map.of("lat", String.valueOf(city.getLatitude()),
                        "lon", String.valueOf(city.getLongitude())));
         weatherCity= new WeatherCity(normalized,weatherResponse);
         redisTemplate.opsForValue().set(cacheKey,weatherCity,TTL);
        weatherLogService.saveLog(new WeatherLog(LocalDateTime.now(),weatherCity));
        return weatherCity;
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
