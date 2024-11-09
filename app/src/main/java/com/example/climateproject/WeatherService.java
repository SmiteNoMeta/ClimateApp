package com.example.climateproject;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {
    private static final String API_KEY = "a25bd05060c8f844b03616c36f29f465";
    private final OkHttpClient client;

    public WeatherService(MainActivity activity) {
        this.client = new OkHttpClient();
    }

    public void fetchWeatherDataByLocation(TextView cityTextView, TextView temperatureTextView, TextView weatherDetailsTextView, double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    parseWeatherData(responseData, cityTextView, temperatureTextView, weatherDetailsTextView);
                } else {
                    cityTextView.post(() -> cityTextView.setText("Błąd pobierania danych"));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                cityTextView.post(() -> cityTextView.setText("Błąd połączenia"));
            }
        });
    }

    private void parseWeatherData(String jsonData, TextView cityTextView, TextView temperatureTextView, TextView weatherDetailsTextView) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String cityName = jsonObject.getString("name");
            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");
            int humidity = main.getInt("humidity");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            String weatherDescription = weatherArray.getJSONObject(0).getString("description");

            cityTextView.post(() -> cityTextView.setText(cityName));
            temperatureTextView.post(() -> temperatureTextView.setText(String.format("%.1f°C", temperature)));
            weatherDetailsTextView.post(() -> weatherDetailsTextView.setText(
                    String.format("Opis: %s\nWilgotność: %d%%\nPrędkość wiatru: %.1f m/s", weatherDescription, humidity, windSpeed)
            ));
        } catch (Exception e) {
            e.printStackTrace();
            cityTextView.post(() -> cityTextView.setText("Błąd przetwarzania danych"));
        }
    }
}
