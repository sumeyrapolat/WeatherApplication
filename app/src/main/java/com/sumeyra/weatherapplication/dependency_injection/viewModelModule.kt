package com.sumeyra.weatherapplication.dependency_injection

import com.sumeyra.weatherapplication.fragment.home.HomeViewModel
import com.sumeyra.weatherapplication.fragment.location.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(weatherDataRepository = get()) }
    viewModel { LocationViewModel(weatherDataRepository = get())}
}