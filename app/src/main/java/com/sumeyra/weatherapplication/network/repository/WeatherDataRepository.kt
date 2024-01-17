package com.sumeyra.weatherapplication.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sumeyra.weatherapplication.data.CurrentLocation
import com.sumeyra.weatherapplication.data.RemoteLocation
import com.sumeyra.weatherapplication.data.RemoteWeatherData
import com.sumeyra.weatherapplication.network.api.WeatherAPI
import java.lang.StringBuilder

class WeatherDataRepository(private val weatherAPI: WeatherAPI)  {

    // telefonun konumunu almamı sağlıyor
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation : CurrentLocation)-> Unit,
        onFailure : ()-> Unit
    ){
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener {location ->
            location ?: onFailure()
            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }.addOnFailureListener{onFailure() }

    }

    // telefonun konumunu aldıktan sonra konumu yazdırma

    @Suppress("DEPRECATION")
    fun updateAddreessText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ) : CurrentLocation{
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation
        return geocoder.getFromLocation(latitude,longitude,1)?.let{addresses->
            val address = addresses[0]
            val addressText = StringBuilder() // StringBuilder, metinleri daha etkili bir şekilde birleştirmek için kullanılır.
            //addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName).append(", ")
            currentLocation.copy(
                location = addressText.toString()
            )
        } ?: currentLocation

    }

    suspend fun searcLocationWeather(query : String): List<RemoteLocation>? {
        val response=weatherAPI.searchLocation(query = query)
        return if (response.isSuccessful) response.body() else null
    }


    suspend fun getWeatherData(latitude: Double,longitude:Double): RemoteWeatherData? {
        val response = weatherAPI.getWeatherData(query = "$latitude,$longitude")
        return if (response.isSuccessful) response.body() else null
    }
}