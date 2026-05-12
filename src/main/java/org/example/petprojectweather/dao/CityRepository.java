package org.example.petprojectweather.dao;

import org.example.petprojectweather.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByCityName(String cityName);

    @Transactional
    void deleteCitiesByCityName(String cityName);
}
