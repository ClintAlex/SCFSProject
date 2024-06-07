package com.springcybtech.scfsproject.services;

import com.springcybtech.scfsproject.dto.WeatherDTO;
import java.util.List;

public interface WeatherService {
    void fetchAndSaveWeather(String city);
    List<WeatherDTO> getWeather(String city);
}
