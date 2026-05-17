package org.example.petprojectweather.rs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.petprojectweather.dto.CityDto;
import org.example.petprojectweather.dto.WeatherCity;
import org.example.petprojectweather.service.CityService;
import org.example.petprojectweather.service.WeatherAPI;
import org.springframework.web.bind.annotation.RestController;

@Path("/weather")
@RestController
public class WeatherResource {

    private final WeatherAPI WeatherAPI;
    private final CityService cityService;

    public WeatherResource(WeatherAPI WeatherAPI, CityService cityService) {
        this.WeatherAPI = WeatherAPI;
        this.cityService = cityService;
    }

    @GET
    @Path("/{city}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getWeatherAroundCity(@PathParam("city") String city){
        WeatherCity weatherCity= WeatherAPI.getWeatherAroundCity(city);
        return Response.ok().entity(weatherCity).build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCity(CityDto cityDto){
        return Response.status(Response.Status.CREATED).entity(cityService.saveNewCity(cityDto)).build();
    }
    @GET
    @Path("/cities")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCites(){
        return Response.ok().entity(cityService.getAllCity()).build();
    }
    @DELETE
    @Path("/{city}")
    @Consumes
    public Response deleteCity(@PathParam("city")String city){
        cityService.deleteCitiesByCityName(city);
        return Response.ok().build();
    }
}
