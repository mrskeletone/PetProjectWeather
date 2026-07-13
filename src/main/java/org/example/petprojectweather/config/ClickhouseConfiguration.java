package org.example.petprojectweather.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
class ClickhouseConfiguration {
    @Value("${clickhouse.jdbc.url}")
    private String URL;
    @Value("${clickhouse.jdbc.user}")
    private String user;
    @Value("${clickhouse.jdbc.password}")
    private String password;
    @Bean
    public DataSource clickhouseDataSource() throws SQLException {
        HikariConfig config= new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        return new HikariDataSource(config);
    }
    @Bean
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource clickhouseDataSource){
        return new JdbcTemplate(clickhouseDataSource);
    }
}
