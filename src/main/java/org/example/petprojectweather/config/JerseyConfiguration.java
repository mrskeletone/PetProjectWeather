package org.example.petprojectweather.config;

import org.example.petprojectweather.mapper.IAEMapper;
import org.example.petprojectweather.rs.WeatherResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JerseyConfiguration {
    @Bean
    public ResourceConfig resourceConfig(){
        ResourceConfig resourceConfig=new ResourceConfig();
        resourceConfig.register(IAEMapper.class);
        resourceConfig.register(WeatherResource.class);
        return resourceConfig;
    }
}
