package com.sumeyra.weatherapplication.fragment.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyra.weatherapplication.data.RemoteLocation
import com.sumeyra.weatherapplication.network.repository.WeatherDataRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val weatherDataRepository: WeatherDataRepository): ViewModel() {

    private val _searchResultDataState= MutableLiveData<SearchResultDataState>()
    val searchResultDataState : LiveData<SearchResultDataState> get() = _searchResultDataState

    //-----------------------------------------------------------------


     fun searchLocationWeather(query: String){
        viewModelScope.launch {
            emitSearchResultUiState(isLoading = true)
            val searchResultDataState= weatherDataRepository.searcLocationWeather(query)
            if (searchResultDataState.isNullOrEmpty()){
                emitSearchResultUiState(error = "Location Not Found! TRY AGAIN")
            }else{
                emitSearchResultUiState(locations = searchResultDataState)
            }
        }
    }

    private fun emitSearchResultUiState(
        isLoading: Boolean=false,
        locations: List<RemoteLocation>? = null,
        error: String? = null
    ){
        val searchResultDataState = SearchResultDataState(isLoading, locations, error)
        _searchResultDataState.value= searchResultDataState
    }

    //Apiden aldığın konum için
    data class SearchResultDataState(
        val isLoading: Boolean,
        val locations: List<RemoteLocation>? ,
        val error: String?
    )

}