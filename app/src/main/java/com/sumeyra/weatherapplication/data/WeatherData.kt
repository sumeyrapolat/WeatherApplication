package com.sumeyra.weatherapplication.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class WeatherData

data class CurrentLocation(
    val date: String = getCurrentDate(),
    val location: String = "Choose Your Location",
    val latitude: Double? = null,
    val longitude: Double?= null
    ) : WeatherData()

data class CurrentWeather(
    val icon: String,
    val humidity : Int,
    val changeOfRain: Int,
    val wind: Float,
    val temperature: Float
): WeatherData()

data class Forecast(
    val time: String,
    val temperature: Float,
    val feelsLikeTemperature: Float,
    val icon: String
): WeatherData()

private fun getCurrentDate(): String{
    val currentDate = Date()
    val formatter = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return "Today ${formatter.format(currentDate)}"
}