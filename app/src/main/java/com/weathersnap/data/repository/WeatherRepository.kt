package com.weathersnap.data.repository

import com.weathersnap.data.remote.WeatherApiService
import com.weathersnap.data.remote.dto.GeocodingResult
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import com.weathersnap.util.WeatherCodeMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {

    suspend fun searchCities(query: String): List<City> {
        val response = apiService.searchCities(name = query)
        return response.results?.map { it.toDomain() } ?: emptyList()
    }

    suspend fun getWeather(cityName: String, latitude: Double, longitude: Double): Weather {
        val response = apiService.getWeather(latitude = latitude, longitude = longitude)
        val current = response.current
            ?: throw Exception("No weather data available")

        return Weather(
            cityName = cityName,
            temperature = current.temperature,
            condition = WeatherCodeMapper.getCondition(current.weatherCode),
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            pressure = current.pressure
        )
    }

    private fun GeocodingResult.toDomain(): City {
        return City(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            country = country ?: "Unknown",
            admin1 = admin1
        )
    }
}
