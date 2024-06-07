package com.springcybtech.scfsproject.observer;

import com.springcybtech.scfsproject.dto.WeatherDTO;

public interface Subject {
    void notifyObservers(WeatherDTO weatherData);
}
