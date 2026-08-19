package com.ritesh.weatherforecastapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;

import com.ritesh.weatherforecastapp.api.RetrofitClient;
import com.ritesh.weatherforecastapp.api.WeatherApi;
import com.ritesh.weatherforecastapp.model.WeatherResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ComponentActivity {

    private EditText cityEditText;
    private Button searchButton;

    private TextView cityTextView;
    private TextView temperatureTextView;
    private TextView weatherTextView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private TextView visibilityTextView;

    private WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeViews();

        weatherApi = RetrofitClient.getWeatherApi();

        searchButton.setOnClickListener(v -> searchWeather());
    }

    private void initializeViews() {

        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.searchButton);

        cityTextView = findViewById(R.id.cityTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        weatherTextView = findViewById(R.id.weatherTextView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);

        humidityTextView = findViewById(R.id.humidityTextView);
        windTextView = findViewById(R.id.windTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        visibilityTextView = findViewById(R.id.visibilityTextView);
    }

    private void searchWeather() {

        String city = cityEditText.getText()
                .toString()
                .trim();

        if (TextUtils.isEmpty(city)) {

            cityEditText.setError("Enter a city name");
            cityEditText.requestFocus();

            return;
        }

        String apiKey =
                com.ritesh.weatherforecastapp.BuildConfig.WEATHER_API_KEY;

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

        Call<WeatherResponse> call =
                weatherApi.getCurrentWeather(
                        city,
                        apiKey,
                        "metric"
                );

        call.enqueue(new Callback<WeatherResponse>() {

            @Override
            public void onResponse(
                    Call<WeatherResponse> call,
                    Response<WeatherResponse> response) {

                searchButton.setEnabled(true);
                searchButton.setText("Search");

                if (response.isSuccessful()
                        && response.body() != null) {

                    updateWeatherUI(response.body());

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
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void handleApiError(Response<WeatherResponse> response) {

        int code = response.code();

        String message;

        switch (code) {

            case 400:
                message = "Invalid request";
                break;

            case 401:
                message = "Invalid or inactive API key";
                break;

            case 404:
                message = "City not found";
                break;

            case 429:
                message = "API request limit exceeded";
                break;

            case 500:
                message = "Weather server error";
                break;

            default:
                message = "API error: HTTP " + code;
                break;
        }

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void updateWeatherUI(WeatherResponse weather) {

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

        cityTextView.setText(weather.getCityName());

        WeatherResponse.MainData main =
                weather.getMain();

        WeatherResponse.WeatherData currentWeather =
                weather.getWeather().get(0);

        WeatherResponse.WindData wind =
                weather.getWind();

        temperatureTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f°C",
                        main.getTemperature()
                )
        );

        weatherTextView.setText(
                capitalize(currentWeather.getDescription())
        );

        feelsLikeTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "Feels like %.0f°C",
                        main.getFeelsLike()
                )
        );

        humidityTextView.setText(
                main.getHumidity() + "%"
        );

        windTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f m/s",
                        wind.getSpeed()
                )
        );

        pressureTextView.setText(
                main.getPressure() + " hPa"
        );

        visibilityTextView.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f km",
                        weather.getVisibility() / 1000.0
                )
        );
    }

    private String capitalize(String text) {

        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.substring(0, 1).toUpperCase()
                + text.substring(1);
    }
}