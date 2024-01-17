package com.sumeyra.weatherapplication.fragment.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.sumeyra.weatherapplication.data.CurrentLocation
import com.sumeyra.weatherapplication.data.CurrentWeather
import com.sumeyra.weatherapplication.data.Forecast
import com.sumeyra.weatherapplication.data.LiveDataEvent
import com.sumeyra.weatherapplication.network.repository.WeatherDataRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel( private val weatherDataRepository: WeatherDataRepository) : ViewModel() {

    //region Current Location
    private val _currentLocation = MutableLiveData<LiveDataEvent<CurrentLocationDataState>>() //mutable
    val currentLocation: LiveData<LiveDataEvent<CurrentLocationDataState>> get() = _currentLocation //immutable


     fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ){
        viewModelScope.launch {
            emitCurrentLocationUIState(isLoading = true)
            weatherDataRepository.getCurrentLocation(
                fusedLocationProviderClient = fusedLocationProviderClient,
                onSuccess = { currentLocation ->
                    updateAddressText(currentLocation,geocoder)
                },
                onFailure = {
                    emitCurrentLocationUIState(error = "Unable to fetch current location")
                }
            )
        }
    }


    private fun updateAddressText(currentLocation: CurrentLocation, geocoder: Geocoder) {
        viewModelScope.launch {
            runCatching {
                weatherDataRepository.updateAddreessText(currentLocation,geocoder)
            }.onSuccess { location ->
                emitCurrentLocationUIState(currentLocation = location)
            }.onFailure {
                emitCurrentLocationUIState(
                    currentLocation = currentLocation.copy(
                        location = "N/A"
                    )
                )
            }

        }
    }

    //Konum için varsayılan değerler verir sonra okuduğum veriye bilgi gelirse onun value larını alır ve kendini günceller ui verirkenki durumu gösterir
    private fun emitCurrentLocationUIState(
        isLoading: Boolean =false,
        currentLocation: CurrentLocation? = null,
        error: String? = null
    ) {
        val currentLocationDataState = CurrentLocationDataState(isLoading,currentLocation,error)
        _currentLocation.value = LiveDataEvent(currentLocationDataState)
    }

    // Bu sınıf, konum bilgisinin durumunu temsil eder
     data class CurrentLocationDataState(
         val isLoading : Boolean,
         val currentLocation: CurrentLocation?,
         val error: String?
 )

    //endregion

    //region Weather Data

    private val _weatherData = MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData  : LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData


    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            emitCurrentLocationUIState(isLoading = true)
            weatherDataRepository.getWeatherData(latitude, longitude)?.let { weatherData->
                emitWeatherDataUiState(
                    currentWeather = CurrentWeather(
                        icon = weatherData.current.condition.icon,
                        temperature = weatherData.current.temperature,
                        wind = weatherData.current.wind,
                        humidity = weatherData.current.humidity,
                        changeOfRain = weatherData.forecast.forecastDay.first().day.changeOfRain
                    ),
                    forecast = weatherData.forecast.forecastDay.first().hour.map {
                        Forecast(
                            time = getForecastTime(it.time),
                            temperature = it.temperature,
                            feelsLikeTemperature = it.fellsLikeTemperature,
                            icon = it.condition.icon
                        )
                    }

                )
            } ?: emitWeatherDataUiState(error = "Unable to fetch weather data ")
        }
    }

    private fun emitWeatherDataUiState(
        isLoading: Boolean = false,
        currentWeather: CurrentWeather? = null,
        forecast: List<Forecast>?= null,
        error: String ?= null
    ){
        val weatherDataState = WeatherDataState(isLoading,currentWeather,forecast, error)
        _weatherData.value= LiveDataEvent(weatherDataState)
    }

    data class WeatherDataState(
        val isLoading: Boolean,
        val currentWeather: CurrentWeather?,
        val forecast: List<Forecast>?,
        val error: String?
    )

    private fun getForecastTime(dateTime: String): String{
        val pattern = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = pattern.parse(dateTime) ?: return dateTime
        return SimpleDateFormat("HH:mm",Locale.getDefault()).format(date)
    }

    //endregion


}