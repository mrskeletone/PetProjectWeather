package org.example.petprojectweather.service;

import org.example.petprojectweather.repository.WeatherLogRepository;
import org.example.petprojectweather.dto.WeatherLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WeatherLogService {
    private final WeatherLogRepository weatherLogRepository;

    public WeatherLogService(WeatherLogRepository weatherLogRepository) {
        this.weatherLogRepository = weatherLogRepository;
    }
    @Async
    public void saveLog(WeatherLog weatherLog){
        weatherLogRepository.save(weatherLog);
    }
}
