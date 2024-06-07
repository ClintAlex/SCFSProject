package com.springcybtech.scfsproject.repository;

import com.springcybtech.scfsproject.models.WeatherModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherModel, Long> {
    List<WeatherModel> findByCity(String city);
    Optional<WeatherModel> findByCityAndTime(String city, Long time);
}
