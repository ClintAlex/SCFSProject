package com.springcybtech.scfsproject.observer;

import com.springcybtech.scfsproject.dto.WeatherDTO;

public interface Observer {
    void update(WeatherDTO weatherData);
}
