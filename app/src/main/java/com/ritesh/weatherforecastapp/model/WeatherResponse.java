package com.ritesh.weatherforecastapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private MainData main;

    @SerializedName("weather")
    private List<WeatherData> weather;

    @SerializedName("wind")
    private WindData wind;

    @SerializedName("visibility")
    private int visibility;

    public String getCityName() {
        return cityName;
    }

    public MainData getMain() {
        return main;
    }

    public List<WeatherData> getWeather() {
        return weather;
    }

    public WindData getWind() {
        return wind;
    }

    public int getVisibility() {
        return visibility;
    }

    public static class MainData {

        @SerializedName("temp")
        private double temperature;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("humidity")
        private int humidity;

        @SerializedName("pressure")
        private int pressure;

        public double getTemperature() {
            return temperature;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getPressure() {
            return pressure;
        }
    }

    public static class WeatherData {

        @SerializedName("main")
        private String condition;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getCondition() {
            return condition;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class WindData {

        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }
}