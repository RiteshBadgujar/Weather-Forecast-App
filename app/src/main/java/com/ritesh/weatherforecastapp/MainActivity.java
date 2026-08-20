package com.ritesh.weatherforecastapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.ritesh.weatherforecastapp.api.RetrofitClient;
import com.ritesh.weatherforecastapp.api.WeatherApi;
import com.ritesh.weatherforecastapp.model.ForecastResponse;
import com.ritesh.weatherforecastapp.model.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ComponentActivity {

    // =========================================================
    // CURRENT WEATHER VIEWS
    // =========================================================

    private EditText cityEditText;
    private Button searchButton;

    private TextView cityTextView;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView weatherTextView;
    private TextView feelsLikeTextView;

    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private TextView visibilityTextView;

    // =========================================================
    // 5-DAY FORECAST VIEWS
    // =========================================================

    private TextView[] forecastDayTextViews;
    private ImageView[] forecastIconImageViews;
    private TextView[] forecastTempTextViews;

    // =========================================================
    // API
    // =========================================================

    private WeatherApi weatherApi;

    // =========================================================
    // OPENWEATHER ICON URL
    // =========================================================

    private static final String WEATHER_ICON_URL =
            "https://openweathermap.org/img/wn/%s@2x.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        initializeViews();

        weatherApi = RetrofitClient.getWeatherApi();

        searchButton.setOnClickListener(
                v -> searchWeather()
        );
    }

    // =========================================================
    // INITIALIZE ALL VIEWS
    // =========================================================

    private void initializeViews() {

        // Current weather
        cityEditText =
                findViewById(R.id.cityEditText);

        searchButton =
                findViewById(R.id.searchButton);

        cityTextView =
                findViewById(R.id.cityTextView);

        weatherIconImageView =
                findViewById(R.id.weatherIconImageView);

        temperatureTextView =
                findViewById(R.id.temperatureTextView);

        weatherTextView =
                findViewById(R.id.weatherTextView);

        feelsLikeTextView =
                findViewById(R.id.feelsLikeTextView);

        humidityTextView =
                findViewById(R.id.humidityTextView);

        windTextView =
                findViewById(R.id.windTextView);

        pressureTextView =
                findViewById(R.id.pressureTextView);

        visibilityTextView =
                findViewById(R.id.visibilityTextView);

        // Forecast days
        forecastDayTextViews = new TextView[]{
                findViewById(R.id.forecastDay1TextView),
                findViewById(R.id.forecastDay2TextView),
                findViewById(R.id.forecastDay3TextView),
                findViewById(R.id.forecastDay4TextView),
                findViewById(R.id.forecastDay5TextView)
        };

        // Forecast icons
        forecastIconImageViews = new ImageView[]{
                findViewById(R.id.forecastIcon1ImageView),
                findViewById(R.id.forecastIcon2ImageView),
                findViewById(R.id.forecastIcon3ImageView),
                findViewById(R.id.forecastIcon4ImageView),
                findViewById(R.id.forecastIcon5ImageView)
        };

        // Forecast temperatures
        forecastTempTextViews = new TextView[]{
                findViewById(R.id.forecastTemp1TextView),
                findViewById(R.id.forecastTemp2TextView),
                findViewById(R.id.forecastTemp3TextView),
                findViewById(R.id.forecastTemp4TextView),
                findViewById(R.id.forecastTemp5TextView)
        };
    }

    // =========================================================
    // SEARCH WEATHER
    // =========================================================

    private void searchWeather() {

        String city = cityEditText
                .getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(city)) {

            cityEditText.setError(
                    "Enter a city name"
            );

            cityEditText.requestFocus();

            return;
        }

        String apiKey =
                BuildConfig.WEATHER_API_KEY;

        if (TextUtils.isEmpty(apiKey)) {

            Toast.makeText(
                    this,
                    "API key is missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        searchButton.setEnabled(false);
        searchButton.setText("Loading...");

        // =====================================================
        // CURRENT WEATHER REQUEST
        // =====================================================

        Call<WeatherResponse> currentWeatherCall =
                weatherApi.getCurrentWeather(
                        city,
                        apiKey,
                        "metric"
                );

        currentWeatherCall.enqueue(
                new Callback<WeatherResponse>() {

                    @Override
                    public void onResponse(
                            Call<WeatherResponse> call,
                            Response<WeatherResponse> response) {

                        searchButton.setEnabled(true);
                        searchButton.setText("Search");

                        if (response.isSuccessful()
                                && response.body() != null) {

                            updateWeatherUI(
                                    response.body()
                            );

                            // Get 5-day forecast
                            getFiveDayForecast(
                                    city,
                                    apiKey
                            );

                        } else {

                            handleApiError(response);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<WeatherResponse> call,
                            Throwable t) {

                        searchButton.setEnabled(true);
                        searchButton.setText("Search");

                        Toast.makeText(
                                MainActivity.this,
                                "Network error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // CURRENT WEATHER ERROR
    // =========================================================

    private void handleApiError(
            Response<WeatherResponse> response) {

        int code = response.code();

        String message;

        switch (code) {

            case 400:
                message = "Invalid request";
                break;

            case 401:
                message =
                        "Invalid or inactive API key";
                break;

            case 404:
                message = "City not found";
                break;

            case 429:
                message =
                        "API request limit exceeded";
                break;

            case 500:
                message =
                        "Weather server error";
                break;

            default:
                message =
                        "API error: HTTP " + code;
                break;
        }

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    // =========================================================
    // UPDATE CURRENT WEATHER UI
    // =========================================================

    private void updateWeatherUI(
            WeatherResponse weather) {

        if (weather.getMain() == null
                || weather.getWeather() == null
                || weather.getWeather().isEmpty()
                || weather.getWind() == null) {

            Toast.makeText(
                    this,
                    "Invalid weather data received",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        cityTextView.setText(
                weather.getCityName()
        );

        WeatherResponse.MainData main =
                weather.getMain();

        WeatherResponse.WeatherData currentWeather =
                weather.getWeather().get(0);

        WeatherResponse.WindData wind =
                weather.getWind();

        // Temperature
        temperatureTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f°C",
                        main.getTemperature()
                )
        );

        // Weather description
        weatherTextView.setText(
                capitalize(
                        currentWeather.getDescription()
                )
        );

        // Feels like
        feelsLikeTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "Feels like %.0f°C",
                        main.getFeelsLike()
                )
        );

        // Humidity
        humidityTextView.setText(
                main.getHumidity() + "%"
        );

        // Wind
        windTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f m/s",
                        wind.getSpeed()
                )
        );

        // Pressure
        pressureTextView.setText(
                main.getPressure() + " hPa"
        );

        // Visibility
        visibilityTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f km",
                        weather.getVisibility()
                                / 1000.0
                )
        );

        // Current weather icon
        loadWeatherIcon(
                currentWeather.getIcon(),
                weatherIconImageView
        );
    }

    // =========================================================
    // 5-DAY FORECAST API
    // =========================================================

    private void getFiveDayForecast(
            String city,
            String apiKey) {

        Call<ForecastResponse> forecastCall =
                weatherApi.getFiveDayForecast(
                        city,
                        apiKey,
                        "metric"
                );

        forecastCall.enqueue(
                new Callback<ForecastResponse>() {

                    @Override
                    public void onResponse(
                            Call<ForecastResponse> call,
                            Response<ForecastResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            processForecast(
                                    response.body()
                            );

                        } else {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Forecast API error: HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ForecastResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                MainActivity.this,
                                "Forecast network error: "
                                        + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // =========================================================
    // PROCESS 5-DAY FORECAST
    // =========================================================

    private void processForecast(
            ForecastResponse forecast) {

        if (forecast == null
                || forecast.getForecastList() == null
                || forecast.getForecastList().isEmpty()) {

            Toast.makeText(
                    this,
                    "No forecast data available",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        List<ForecastResponse.ForecastItem> forecastList =
                forecast.getForecastList();

        // =====================================================
        // Determine timezone
        // =====================================================

        TimeZone timeZone =
                TimeZone.getDefault();

        if (forecast.getCity() != null) {

            int timezoneSeconds =
                    forecast.getCity().getTimezone();

            timeZone =
                    TimeZone.getTimeZone(
                            "GMT"
                                    + (timezoneSeconds >= 0
                                    ? "+"
                                    : "")
                                    + (timezoneSeconds / 3600)
                    );
        }

        // =====================================================
        // Group forecast records by date
        // =====================================================

        Map<String,
                List<ForecastResponse.ForecastItem>>
                dailyForecast =
                new LinkedHashMap<>();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setTimeZone(timeZone);

        for (ForecastResponse.ForecastItem item
                : forecastList) {

            Date date =
                    new Date(
                            item.getTimestamp()
                                    * 1000L
                    );

            String dateKey =
                    dateFormat.format(date);

            if (!dailyForecast.containsKey(dateKey)) {

                dailyForecast.put(
                        dateKey,
                        new ArrayList<>()
                );
            }

            List<ForecastResponse.ForecastItem>
                    itemsForDate =
                    dailyForecast.get(dateKey);

            if (itemsForDate != null) {
                itemsForDate.add(item);
            }
        }

        // =====================================================
        // Display first five days
        // =====================================================

        int dayIndex = 0;

        for (Map.Entry<String,
                List<ForecastResponse.ForecastItem>> entry
                : dailyForecast.entrySet()) {

            if (dayIndex >= 5) {
                break;
            }

            updateForecastDay(
                    dayIndex,
                    entry.getKey(),
                    entry.getValue(),
                    timeZone
            );

            dayIndex++;
        }

        // =====================================================
        // Clear unused forecast rows
        // =====================================================

        for (int i = dayIndex; i < 5; i++) {

            forecastDayTextViews[i]
                    .setText("--");

            forecastIconImageViews[i]
                    .setImageDrawable(null);

            forecastTempTextViews[i]
                    .setText("--° / --°");
        }
    }

    // =========================================================
    // UPDATE ONE FORECAST DAY
    // =========================================================

    private void updateForecastDay(
            int index,
            String dateKey,
            List<ForecastResponse.ForecastItem> items,
            TimeZone timeZone) {

        if (items == null || items.isEmpty()) {
            return;
        }

        double minTemperature =
                Double.MAX_VALUE;

        double maxTemperature =
                -Double.MAX_VALUE;

        ForecastResponse.ForecastItem selectedItem =
                items.get(0);

        int closestToNoon =
                Integer.MAX_VALUE;

        // =====================================================
        // Find daily min/max
        // =====================================================

        for (ForecastResponse.ForecastItem item
                : items) {

            if (item.getMain() == null) {
                continue;
            }

            double min =
                    item.getMain()
                            .getMinTemperature();

            double max =
                    item.getMain()
                            .getMaxTemperature();

            if (min < minTemperature) {
                minTemperature = min;
            }

            if (max > maxTemperature) {
                maxTemperature = max;
            }

            // =================================================
            // Select weather condition closest to 12 PM
            // =================================================

            Date date =
                    new Date(
                            item.getTimestamp()
                                    * 1000L
                    );

            SimpleDateFormat hourFormat =
                    new SimpleDateFormat(
                            "H",
                            Locale.getDefault()
                    );

            hourFormat.setTimeZone(
                    timeZone
            );

            int hour =
                    Integer.parseInt(
                            hourFormat.format(date)
                    );

            int difference =
                    Math.abs(hour - 12);

            if (difference < closestToNoon) {

                closestToNoon = difference;

                selectedItem = item;
            }
        }

        // =====================================================
        // Day name
        // =====================================================

        String dayName =
                getDayName(
                        dateKey,
                        index,
                        timeZone
                );

        forecastDayTextViews[index]
                .setText(dayName);

        // =====================================================
        // Weather icon
        // =====================================================

        String iconCode = null;

        if (selectedItem.getWeather() != null
                && !selectedItem.getWeather().isEmpty()) {

            iconCode =
                    selectedItem
                            .getWeather()
                            .get(0)
                            .getIcon();
        }

        loadWeatherIcon(
                iconCode,
                forecastIconImageViews[index]
        );

        // =====================================================
        // Temperature
        // =====================================================

        if (minTemperature == Double.MAX_VALUE
                && selectedItem.getMain() != null) {

            minTemperature =
                    selectedItem
                            .getMain()
                            .getTemperature();
        }

        if (maxTemperature == -Double.MAX_VALUE
                && selectedItem.getMain() != null) {

            maxTemperature =
                    selectedItem
                            .getMain()
                            .getTemperature();
        }

        forecastTempTextViews[index]
                .setText(
                        String.format(
                                Locale.getDefault(),
                                "%.0f° / %.0f°",
                                maxTemperature,
                                minTemperature
                        )
                );
    }

    // =========================================================
    // LOAD WEATHER ICON USING GLIDE
    // =========================================================

    private void loadWeatherIcon(
            String iconCode,
            ImageView imageView) {

        if (imageView == null) {
            return;
        }

        if (TextUtils.isEmpty(iconCode)) {

            imageView.setImageDrawable(null);

            return;
        }

        String iconUrl =
                String.format(
                        Locale.getDefault(),
                        WEATHER_ICON_URL,
                        iconCode
                );

        Glide.with(this)
                .load(iconUrl)
                .into(imageView);
    }

    // =========================================================
    // GET DAY NAME
    // =========================================================

    private String getDayName(
            String dateKey,
            int index,
            TimeZone timeZone) {

        if (index == 0) {
            return "Today";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            inputFormat.setTimeZone(
                    timeZone
            );

            Date date =
                    inputFormat.parse(dateKey);

            if (date == null) {
                return dateKey;
            }

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "EEE",
                            Locale.getDefault()
                    );

            outputFormat.setTimeZone(
                    timeZone
            );

            return outputFormat.format(date);

        } catch (Exception e) {

            return dateKey;
        }
    }

    // =========================================================
    // CAPITALIZE WEATHER DESCRIPTION
    // =========================================================

    private String capitalize(String text) {

        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.substring(0, 1).toUpperCase()
                + text.substring(1);
    }
}