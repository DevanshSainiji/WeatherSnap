package com.weathersnap.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SuggestionUiState {
    data object Idle : SuggestionUiState()
    data object Loading : SuggestionUiState()
    data class Success(val cities: List<City>) : SuggestionUiState()
    data object Empty : SuggestionUiState()
    data class Error(val message: String) : SuggestionUiState()
}

sealed class WeatherUiState {
    data object Idle : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _suggestionState = MutableStateFlow<SuggestionUiState>(SuggestionUiState.Idle)
    val suggestionState: StateFlow<SuggestionUiState> = _suggestionState.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _selectedCity = MutableStateFlow<City?>(null)
    val selectedCity: StateFlow<City?> = _selectedCity.asStateFlow()

    // Cache city suggestions to avoid repeated API calls
    private val suggestionCache = mutableMapOf<String, List<City>>()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCity.value = null

        if (query.length <= 2) {
            _suggestionState.value = SuggestionUiState.Idle
            return
        }

        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            searchCities(query)
        }
    }

    private suspend fun searchCities(query: String) {
        // Check cache first
        val cached = suggestionCache[query.lowercase()]
        if (cached != null) {
            _suggestionState.value = if (cached.isEmpty()) {
                SuggestionUiState.Empty
            } else {
                SuggestionUiState.Success(cached)
            }
            return
        }

        _suggestionState.value = SuggestionUiState.Loading
        try {
            val cities = weatherRepository.searchCities(query)
            // Cache the result
            suggestionCache[query.lowercase()] = cities
            _suggestionState.value = if (cities.isEmpty()) {
                SuggestionUiState.Empty
            } else {
                SuggestionUiState.Success(cities)
            }
        } catch (e: Exception) {
            _suggestionState.value = SuggestionUiState.Error(
                e.message ?: "Failed to search cities"
            )
        }
    }

    fun onCitySelected(city: City) {
        _selectedCity.value = city
        _searchQuery.value = city.displayName
        _suggestionState.value = SuggestionUiState.Idle
    }

    fun fetchWeather() {
        val city = _selectedCity.value ?: return

        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val weather = weatherRepository.getWeather(
                    cityName = city.displayName,
                    latitude = city.latitude,
                    longitude = city.longitude
                )
                _weatherState.value = WeatherUiState.Success(weather)
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(
                    e.message ?: "Failed to fetch weather"
                )
            }
        }
    }
}
