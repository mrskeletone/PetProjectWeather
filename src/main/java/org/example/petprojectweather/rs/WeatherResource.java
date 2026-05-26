package org.example.petprojectweather.rs;


import org.example.petprojectweather.dto.CityDto;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.entity.City;
import org.example.petprojectweather.service.CityService;
import org.example.petprojectweather.service.WeatherAPI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/weather")
@RestController
public class WeatherResource {

    private final WeatherAPI WeatherAPI;
    private final CityService cityService;

    public WeatherResource(WeatherAPI WeatherAPI, CityService cityService) {
        this.WeatherAPI = WeatherAPI;
        this.cityService = cityService;
    }

    @GetMapping(value = "/{city}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherCity> getWeatherAroundCity(@PathVariable("city") String city){
        WeatherCity weatherCity= WeatherAPI.getWeatherAroundCity(city);
        return ResponseEntity.ok(weatherCity);
    }
    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<City> postCity(@RequestBody CityDto cityDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.saveNewCity(cityDto));
    }
    @GetMapping(value = "/cities",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<City>> getAllCites(){
        return ResponseEntity.ok(cityService.getAllCity());
    }
    @DeleteMapping("/{city}")
    public ResponseEntity<Void> deleteCity(@PathVariable("city")String city){
        cityService.deleteCitiesByCityName(city);
        return ResponseEntity.ok().build();
    }
}
