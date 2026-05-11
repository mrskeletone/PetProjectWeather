package org.example.petprojectweather.service;

import org.example.petprojectweather.dao.CityRepository;
import org.example.petprojectweather.dto.CityDto;
import org.example.petprojectweather.entity.City;
import org.example.petprojectweather.mapper.CityMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    public List<City> getAllCity(){
        return cityRepository.findAll();
    }
    public City getCityByCityName(String name){
        Optional<City> optionalCity=cityRepository.findByCityName(name);
        if(optionalCity.isPresent()){
            return optionalCity.get();
        }else{
            throw  new IllegalArgumentException("Неизвестный город "+name);
        }
    }
    public City saveNewCity(CityDto city){
        return cityRepository.save(CityMapper.dtoToCity(city));
    }

}
