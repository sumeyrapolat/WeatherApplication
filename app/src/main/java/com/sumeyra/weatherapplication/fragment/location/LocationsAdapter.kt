package com.sumeyra.weatherapplication.fragment.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.sumeyra.weatherapplication.data.RemoteLocation
import com.sumeyra.weatherapplication.databinding.ItemContianerLocationBinding

class LocationsAdapter(
        private val onLocationClicked :(RemoteLocation) -> Unit
): RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder>() {

    private val locations = mutableListOf<RemoteLocation>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data : List<RemoteLocation>){
        locations.clear()
        locations.addAll(data)
        notifyDataSetChanged()
    }

    inner class LocationsViewHolder( private val binding: ItemContianerLocationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(remoteLocation: RemoteLocation){
            with(remoteLocation){
                val location = "${name} ,${country}"
                binding.textRemoteLocation.text = location
                binding.root.setOnClickListener { onLocationClicked(remoteLocation) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsViewHolder {
        return LocationsViewHolder(
            ItemContianerLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: LocationsViewHolder, position: Int) {
        holder.bind(remoteLocation =  locations[position])
    }


}