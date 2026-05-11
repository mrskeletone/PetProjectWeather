package org.example.petprojectweather.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.service.WeatherAPI;
import org.springframework.web.bind.annotation.RestController;

@Path("/weather")
@RestController
public class WeatherResource {

    private final WeatherAPI weatherAPI;

    public WeatherResource(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getWeatherAroundCity(@QueryParam("city") String city){
        WeatherCity weatherCity=weatherAPI.getWeatherAroundCity(city);
        return Response.ok().entity(weatherCity).build();
    }
}
