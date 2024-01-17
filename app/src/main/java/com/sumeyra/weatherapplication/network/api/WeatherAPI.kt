package com.sumeyra.weatherapplication.network.api

import com.sumeyra.weatherapplication.data.RemoteLocation
import com.sumeyra.weatherapplication.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    //58960d8ffa1049b8836172025241201
    //https://api.weatherapi.com/v1/search.json?key=58960d8ffa1049b8836172025241201&q=London

    companion object{
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = "58960d8ffa1049b8836172025241201"
    }

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key :String = API_KEY,
        @Query("q") query: String
    ): Response<List<RemoteLocation>>


    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key :String = API_KEY,
        @Query("q") query: String
    ) : Response<RemoteWeatherData>


}