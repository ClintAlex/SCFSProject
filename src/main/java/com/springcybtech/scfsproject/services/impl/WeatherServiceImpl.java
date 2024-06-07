package com.springcybtech.scfsproject.services.impl;

import com.springcybtech.scfsproject.dto.WeatherDTO;
import com.springcybtech.scfsproject.models.WeatherModel;
import com.springcybtech.scfsproject.observer.Observer;
import com.springcybtech.scfsproject.observer.Subject;
import com.springcybtech.scfsproject.repository.WeatherRepository;
import com.springcybtech.scfsproject.services.WeatherService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService, Subject {

    @Autowired
    private WeatherRepository weatherRepository;

    private static String API_KEY = "59c6824ca3e5df64f9ff5e40c95622b5";
    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=" + API_KEY + "&units=metric";

    private AtomicReference<String> currentCity = new AtomicReference<>("Odense");
    private List<Observer> observers = new ArrayList<>();

    @Override
    public synchronized void fetchAndSaveWeather(String city) {
        currentCity.set(city);
        String url = String.format(URL, city);
        String jsonResponse = fetchWeatherData(url);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        WeatherModel weatherModel = parseWeatherData(jsonObject, city);

        if (!isDuplicate(weatherModel)) {
            weatherRepository.save(weatherModel);
            System.out.println("Weather data saved for " + city + " at " + weatherModel.getTime());

            WeatherDTO weatherData = convertToDTO(weatherModel);
            notifyObservers(weatherData);
        } else {
            System.out.println("Duplicate data found for " + city + " at " + weatherModel.getTime() + ", skipping save.");
        }
    }

    private String fetchWeatherData(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    private WeatherModel parseWeatherData(JSONObject jsonObject, String city) {
        Long time = jsonObject.getLong("dt");
        Double temperature = jsonObject.getJSONObject("main").getDouble("temp");
        Integer humidity = jsonObject.getJSONObject("main").getInt("humidity");
        Double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
        Integer windDirection = jsonObject.getJSONObject("wind").getInt("deg");
        String locationName = jsonObject.getString("name");
        JSONArray weatherArray = jsonObject.getJSONArray("weather");
        String icon = weatherArray.getJSONObject(0).getString("icon");

        return WeatherModel.builder()
            .city(city)
            .time(time)
            .temperature(temperature)
            .humidity(humidity)
            .windSpeed(windSpeed)
            .windDirection(windDirection)
            .location(locationName)
            .icon(icon)
            .build();
    }

    private boolean isDuplicate(WeatherModel weatherModel) {
        Optional<WeatherModel> existingWeather = weatherRepository.findByCityAndTime(weatherModel.getCity(), weatherModel.getTime());
        return existingWeather.isPresent();
    }

    @Override
    public List<WeatherDTO> getWeather(String city) {
        return weatherRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WeatherDTO convertToDTO(WeatherModel weatherModel) {
        return WeatherDTO.builder()
            .city(weatherModel.getCity())
            .time(weatherModel.getTime())
            .temperature(weatherModel.getTemperature())
            .humidity(weatherModel.getHumidity())
            .windSpeed(weatherModel.getWindSpeed())
            .windDirection(weatherModel.getWindDirection())
            .location(weatherModel.getLocation())
            .icon(weatherModel.getIcon())
            .build();
    }

    public String getCurrentCity() {
        return currentCity.get();
    }

    @Override
    public void notifyObservers(WeatherDTO weatherData) {
        for (Observer observer : observers) {
            observer.update(weatherData);
        }
    }
}
