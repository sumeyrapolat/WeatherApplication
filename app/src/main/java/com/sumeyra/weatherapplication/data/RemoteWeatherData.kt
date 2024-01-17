package com.sumeyra.weatherapplication.data

import com.google.gson.annotations.SerializedName

data class RemoteWeatherData(
    val current: CurrentWeatherRemote,
    val forecast : ForecastRemote
)
data class ForecastRemote(
    @SerializedName("forecastday") val forecastDay: List<ForecastDayRemote>
)


data class CurrentWeatherRemote(
    @SerializedName("temp_c") val temperature : Float,
    val condition : WeatherConditionRemote,
    @SerializedName("wind_kph") val wind : Float,
    val humidity: Int,

    )

data class ForecastDayRemote(
    val day : DayRemote,
    val hour: List<ForecastHourRemote>

)

data class DayRemote(
    @SerializedName("daily_chance_of_rain") val changeOfRain: Int
)

data class ForecastHourRemote(
    val time : String,
    @SerializedName("temp_c") val temperature : Float,
    @SerializedName("feelslike_c") val fellsLikeTemperature: Float,
    val condition: WeatherConditionRemote
)


data class WeatherConditionRemote(
    val icon : String
)