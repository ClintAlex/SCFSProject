package com.springcybtech.scfsproject.scheduling;

import com.springcybtech.scfsproject.dto.WeatherDTO;
import com.springcybtech.scfsproject.services.impl.WeatherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class WeatherScheduler {

    @Autowired
    private WeatherServiceImpl weatherService;

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 60000)
    public void fetchWeatherData() {
        String city = weatherService.getCurrentCity();
        weatherService.fetchAndSaveWeather(city);
        List<WeatherDTO> weatherData = weatherService.getWeather(city);
        template.convertAndSend("/topic/weather/" + city, weatherData);
    }
}
