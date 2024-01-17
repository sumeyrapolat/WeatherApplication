package com.sumeyra.weatherapplication.utils

import  android.app.Application
import com.sumeyra.weatherapplication.dependency_injection.networkModule
import com.sumeyra.weatherapplication.dependency_injection.repositoryModule
import com.sumeyra.weatherapplication.dependency_injection.serializerModule
import com.sumeyra.weatherapplication.dependency_injection.storageModule
import com.sumeyra.weatherapplication.dependency_injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppConfig : Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(
                listOf(
                repositoryModule,
                viewModelModule,
                serializerModule,
                storageModule,
                networkModule
            ))
        }
    }

}