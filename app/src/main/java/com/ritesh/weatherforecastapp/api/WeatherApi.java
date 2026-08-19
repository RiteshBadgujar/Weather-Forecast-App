package com.ritesh.weatherforecastapp.api;

import com.ritesh.weatherforecastapp.model.ForecastResponse;
import com.ritesh.weatherforecastapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    // Current weather
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    // 5-day weather forecast
    @GET("forecast")
    Call<ForecastResponse> getFiveDayForecast(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}