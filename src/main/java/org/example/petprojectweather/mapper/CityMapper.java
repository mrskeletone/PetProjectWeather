package org.example.petprojectweather.mapper;

import org.example.petprojectweather.dto.CityDto;
import org.example.petprojectweather.entity.City;

public class CityMapper {
    public static City dtoToCity(CityDto cityDto){
        return new City(cityDto.getName(),cityDto.getLatitude(),cityDto.getLongitude());
    }
}
