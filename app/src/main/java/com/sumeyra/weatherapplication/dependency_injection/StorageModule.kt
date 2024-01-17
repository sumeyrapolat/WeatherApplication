package com.sumeyra.weatherapplication.dependency_injection

import com.sumeyra.weatherapplication.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single { SharedPreferencesManager(context = get(), gson = get()) }
}