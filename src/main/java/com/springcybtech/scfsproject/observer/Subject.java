package com.springcybtech.scfsproject.observer;

import com.springcybtech.scfsproject.dto.WeatherDTO;

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(WeatherDTO weatherData);
}
