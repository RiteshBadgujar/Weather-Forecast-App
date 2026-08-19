package com.ritesh.weatherforecastapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {

    @SerializedName("list")
    private List<ForecastItem> forecastList;

    @SerializedName("city")
    private CityData city;

    public List<ForecastItem> getForecastList() {
        return forecastList;
    }

    public CityData getCity() {
        return city;
    }

    // =========================================================
    // FORECAST ITEM
    // =========================================================

    public static class ForecastItem {

        @SerializedName("dt")
        private long timestamp;

        @SerializedName("main")
        private MainData main;

        @SerializedName("weather")
        private List<WeatherData> weather;

        @SerializedName("wind")
        private WindData wind;

        public long getTimestamp() {
            return timestamp;
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
    }

    // =========================================================
    // MAIN DATA
    // =========================================================

    public static class MainData {

        @SerializedName("temp")
        private double temperature;

        @SerializedName("temp_min")
        private double minTemperature;

        @SerializedName("temp_max")
        private double maxTemperature;

        @SerializedName("humidity")
        private int humidity;

        public double getTemperature() {
            return temperature;
        }

        public double getMinTemperature() {
            return minTemperature;
        }

        public double getMaxTemperature() {
            return maxTemperature;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    // =========================================================
    // WEATHER DATA
    // =========================================================

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

    // =========================================================
    // WIND DATA
    // =========================================================

    public static class WindData {

        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    // =========================================================
    // CITY DATA
    // =========================================================

    public static class CityData {

        @SerializedName("name")
        private String name;

        @SerializedName("timezone")
        private int timezone;

        public String getName() {
            return name;
        }

        public int getTimezone() {
            return timezone;
        }
    }
}