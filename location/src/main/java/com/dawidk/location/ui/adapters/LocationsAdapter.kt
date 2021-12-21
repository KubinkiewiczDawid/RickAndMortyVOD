package com.dawidk.location.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dawidk.common.utils.setLocationImage
import com.dawidk.core.domain.model.Location
import com.dawidk.location.R
import com.dawidk.location.databinding.LocationViewBinding

class LocationsAdapter :
    PagingDataAdapter<Location, LocationsAdapter.LocationViewHolder>(LocationDiffCallBack()) {

    private class LocationDiffCallBack : DiffUtil.ItemCallback<Location>() {

        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LocationViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        getItem(position)?.let { holder.applyItem(it) }
    }

    class LocationViewHolder(private val binding: LocationViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun applyItem(location: Location) {
            binding.apply {
                tvLocationName.apply {
                    text = location.name
                }
                locationTypeLayout.typeTextView.text = location.type
                dimensionLayout.dimensionTextView.apply {
                    text = location.dimension.let {
                        if (it.isEmpty()) resources.getString(R.string.unknown)
                        else it
                    }
                }
                setLocationImage(locationImageView, location.id, location.name)
            }
        }
    }
}