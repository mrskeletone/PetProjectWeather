package org.example.petprojectweather.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.service.IWeatherAPI;
import org.springframework.web.bind.annotation.RestController;

@Path("/weather")
@RestController
public class WeatherResource {

    private final IWeatherAPI IWeatherAPI;

    public WeatherResource(IWeatherAPI IWeatherAPI) {
        this.IWeatherAPI = IWeatherAPI;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getWeatherAroundCity(@QueryParam("city") String city){
        WeatherCity weatherCity= IWeatherAPI.getWeatherAroundCity(city);
        return Response.ok().entity(weatherCity).build();
    }
}
