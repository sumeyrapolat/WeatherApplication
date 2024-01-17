package com.sumeyra.weatherapplication.fragment.home

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import com.sumeyra.weatherapplication.R
import com.sumeyra.weatherapplication.data.CurrentLocation
import com.sumeyra.weatherapplication.databinding.HomeFragmentBinding
import com.sumeyra.weatherapplication.storage.SharedPreferencesManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment: Fragment() {

    companion object{
        const val REQUEST_KEY_MANUAL_SEARCH_LOCATION = "manualLocationSearch"
        const val KEY_LOCATION_TEXT = "locationText"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
    }

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val homeViewModel: HomeViewModel by viewModel()

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val geocoder by lazy {
        Geocoder(requireContext())
    }

    private val sharedPreferencesManager: SharedPreferencesManager by inject()


    private val weatherDataAdapter= WeatherDataAdapter(
        // konum seçmeye tıklandı
        onLocationClicked = {
            showLocationOptions()
            //Toast.makeText(requireContext(),"Clicked()",Toast.LENGTH_SHORT).show()
        }
    )
    //Permission için gerekli launcher kuruldu ve gerekli koşullar kontrol edildi
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted){
            getCurrentLocation()
        }else{
            Toast.makeText(requireContext(),"Permission Denied!",Toast.LENGTH_SHORT).show()
        }

    }
    private var isInitialLocationSet : Boolean = false

    //---------------------------------------------------------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setListeners()
        if (!isInitialLocationSet){
            setCurrentLocation(currentLocation = sharedPreferencesManager.getCurrentLocation())
            isInitialLocationSet=true
        }
        setObservers()



    }
    private fun setListeners(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            setCurrentLocation(sharedPreferencesManager.getCurrentLocation())
        }
    }

    /* View modelime göre eğer datalarım alındı mı yoksa alınma aşamasında mı ona göre
    yükleniyor ekranı ya da alınan bilgilerin gösterileceği ekranı yazmamız gerekiyor
    ui için observe yap eğer datalar isLoading aşamasındaysa yani alınıyorsa showLoading() yyükleniyor görünsün
     */
    private fun setObservers(){
        with(homeViewModel){
            currentLocation.observe(viewLifecycleOwner){
                val currentLocationDataState = it.getContentIfNotHandled() ?: return@observe
                if (currentLocationDataState.isLoading){
                    showLoading()
                }
                //eğer konum alındıysa hideLoading() olsun ve alınan veriler setWeatherData ile ayarlansın
                currentLocationDataState.currentLocation?.let { currentLocation->
                    hideLoading()
                    sharedPreferencesManager.saveCurrentLocation(currentLocation) //konum bilgisinin kaydedilmesini sağlıyor
                    setCurrentLocation(currentLocation)
                }
                currentLocationDataState.error?.let {error->
                    hideLoading()
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
            weatherData.observe(viewLifecycleOwner){
                val weatherDataState = it.getContentIfNotHandled() ?: return@observe
                binding.swipeRefreshLayout.isRefreshing = weatherDataState.isLoading
                weatherDataState.currentWeather?.let {currentWeather->
                    weatherDataAdapter.setCurrentWeather(currentWeather)
                }

                weatherDataState.forecast?.let { forecasts->
                    weatherDataAdapter.setForecastData(forecasts)
                }
                weatherDataState.error?.let {error->
                    Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun setWeatherDataAdapter(){
        binding.weatherDataRecyclerView.itemAnimator = null
        binding.weatherDataRecyclerView.adapter = weatherDataAdapter
    }

    //eğer bir konum alınabildiyse weatherdata ayarlansın
    private fun setCurrentLocation(currentLocation: CurrentLocation? = null){
        weatherDataAdapter.setCurrentLocation(currentLocation ?: CurrentLocation())
        currentLocation?.let {getWeatherData(currentLocation =it)}
    }



    //Cihazın konumunu almak için;
    private fun getCurrentLocation(){
        //Toast.makeText(requireContext(),"getCurrentLocation()",Toast.LENGTH_SHORT).show()
        homeViewModel.getCurrentLocation(fusedLocationProviderClient,geocoder)
    }


    // izin verildiyse;
    private fun isLocationPermissionGranted(): Boolean{
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    //izin isteme;
    private fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //izin varsa ne yap izin yoksa ne yap;
    private fun proceedWithCurrentLocation(){
        if (isLocationPermissionGranted()){
            getCurrentLocation()
        }else{
            requestPermission()
        }
    }

    //Search Manually için gerekli işlemlerde sonra gideceği komutu yaz
    private fun showLocationOptions(){
        val options= arrayOf("Current Location","Search Manually")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Choose Location")
            setItems(options){ _, which ->
                when(which){
                    0-> proceedWithCurrentLocation()
                    1-> startManualLocationSearch()

                }
            }
            show()
        }

    }

    private fun showLoading(){
        with(binding){
            weatherDataRecyclerView.visibility = View.GONE
            swipeRefreshLayout.isEnabled= false
            swipeRefreshLayout.isRefreshing= true
        }
    }

    private fun hideLoading(){
        with(binding){
            weatherDataRecyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.isEnabled= true
            swipeRefreshLayout.isRefreshing= false
        }
    }


    //----------------MANUEL SEARCH----------------------------------

    private fun startManualLocationSearch(){
        startListeningManualLocationSelection()
        findNavController().navigate(R.id.action_homeFragment_to_locationFragment)
    }

    private fun startListeningManualLocationSelection(){
       setFragmentResultListener(REQUEST_KEY_MANUAL_SEARCH_LOCATION){_, bundle->
           stopListeningManualLocationSelection()
           val currentLocation = CurrentLocation(
               location = bundle.getString(KEY_LOCATION_TEXT) ?: "N/A",
               latitude = bundle.getDouble(KEY_LATITUDE),
               longitude = bundle.getDouble(KEY_LONGITUDE)

           )
           sharedPreferencesManager.saveCurrentLocation(currentLocation)
           setCurrentLocation(currentLocation)
       }
    }


    private fun stopListeningManualLocationSelection(){
        clearFragmentResultListener(REQUEST_KEY_MANUAL_SEARCH_LOCATION)
    }

    private fun getWeatherData(currentLocation: CurrentLocation){
        if(currentLocation.latitude !=null && currentLocation.longitude!=null){
           homeViewModel.getWeatherData(
               latitude = currentLocation.latitude,
               longitude = currentLocation.longitude
           )
        }
    }
}