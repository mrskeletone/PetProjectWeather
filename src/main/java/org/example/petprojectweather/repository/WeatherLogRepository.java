package org.example.petprojectweather.repository;

import org.example.petprojectweather.dto.WeatherLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;

@Repository
public class WeatherLogRepository {
    private final JdbcTemplate clickhouseJdbcTemplate;

    public WeatherLogRepository(@Qualifier("clickhouseJdbcTemplate") JdbcTemplate clickhouseJdbcTemplate) {
        this.clickhouseJdbcTemplate = clickhouseJdbcTemplate;
    }
    private static final DateTimeFormatter CLICKHOUSE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(WeatherLog weatherLog){
        String sql ="INSERT INTO weather_logs (timestamp, city, temperature, wind_speed, latitude, longitude) " +
                "VALUES (?,?,?,?,?,?)";

        clickhouseJdbcTemplate.update(sql,
                weatherLog.timestamp().format(CLICKHOUSE_DATE_FORMAT),
                weatherLog.weatherCity().city(),
                weatherLog.weatherCity().weatherResponseAPIOpenMeteo().current().temperature_2m(),
                weatherLog.weatherCity().weatherResponseAPIOpenMeteo().current().wind_speed_10m(),
                weatherLog.weatherCity().weatherResponseAPIOpenMeteo().latitude(),
                weatherLog.weatherCity().weatherResponseAPIOpenMeteo().longitude());
    }
}
