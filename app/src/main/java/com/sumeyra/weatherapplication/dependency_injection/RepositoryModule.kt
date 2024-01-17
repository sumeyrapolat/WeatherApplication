package com.sumeyra.weatherapplication.dependency_injection

import com.sumeyra.weatherapplication.network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherDataRepository(weatherAPI = get()) }
}