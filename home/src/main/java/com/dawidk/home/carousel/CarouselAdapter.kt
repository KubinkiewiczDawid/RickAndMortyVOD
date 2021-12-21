package com.dawidk.home.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dawidk.common.episodeProgressBar.EpisodeProgressBarHandler
import com.dawidk.common.utils.setAutoSize
import com.dawidk.common.utils.setEpisodeImage
import com.dawidk.common.utils.setLocationImage
import com.dawidk.home.R
import com.dawidk.home.databinding.CharacterCarouselItemBinding
import com.dawidk.home.databinding.EpisodeCarouselItemBinding
import com.dawidk.home.databinding.LocationCarouselItemBinding
import com.dawidk.home.model.CarouselItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TYPE_CHARACTER = 1
private const val TYPE_LOCATION = 2
private const val TYPE_EPISODE = 3
private const val IMAGES_NUMBER = 4

class CarouselAdapter(
    private val _clickEvent: MutableSharedFlow<CarouselItem>,
    private val _clickPlayButtonEvent: MutableSharedFlow<CarouselItem>
) :
    ListAdapter<CarouselItem, RecyclerView.ViewHolder>(
        ItemDiffCallback()
    ), KoinComponent {

    private val episodeProgressBarHandler: EpisodeProgressBarHandler by inject()

    private class ItemDiffCallback : DiffUtil.ItemCallback<CarouselItem>() {

        override fun areItemsTheSame(oldItem: CarouselItem, newItem: CarouselItem): Boolean {
            return when (oldItem) {
                is CarouselItem.CharacterItem -> oldItem == newItem
                is CarouselItem.LocationItem -> oldItem == newItem
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: CarouselItem, newItem: CarouselItem): Boolean {
            return when (oldItem) {
                is CarouselItem.CharacterItem -> oldItem.name == (newItem as CarouselItem.CharacterItem).name
                is CarouselItem.LocationItem -> oldItem.name == (newItem as CarouselItem.LocationItem).name
                else -> (oldItem as CarouselItem.EpisodeItem).name == (newItem as CarouselItem.EpisodeItem).name
            }
        }
    }

    override fun getItem(position: Int): CarouselItem {
        return super.getItem(position % currentList.size)
    }

    fun getOriginalItemCount(): Int {
        return currentList.size
    }

    override fun getItemCount(): Int {
        return currentList.size * 3
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is EpisodeViewHolder -> {
                holder.scope.cancel()
            }
        }
    }

    inner class CharacterViewHolder(private val binding: CharacterCarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setCharacterDetails(characterItem: CarouselItem.CharacterItem) {
            binding.name.apply {
                setCarouselTextAutoSize()
                text = characterItem.name
            }
            binding.image.load(characterItem.image)
        }
    }

    inner class LocationViewHolder(private val binding: LocationCarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setLocationDetails(locationItem: CarouselItem.LocationItem) {
            binding.name.apply {
                setCarouselTextAutoSize()
                text = locationItem.name
            }
            setLocationImage(binding.image, locationItem.id, locationItem.name)
        }
    }

    inner class EpisodeViewHolder(private val binding: EpisodeCarouselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var scope: CoroutineScope

        fun setEpisodeDetails(episodeItem: CarouselItem.EpisodeItem) {
            scope = CoroutineScope(Dispatchers.Main)
            binding.name.apply {
                setCarouselTextAutoSize()
                text = episodeItem.name
            }
            val episodeImage: ImageView = binding.image
            scope.launch {
                setEpisodeImage(
                    episodeImage,
                    episodeImage.context,
                    episodeItem.characters,
                    episodeItem.episode,
                    IMAGES_NUMBER
                )
                episodeProgressBarHandler.setProgressBar(
                    binding.carouselEpisodeProgressBar,
                    episodeItem.id,
                )
            }
        }

        fun setOnPlayButtonClickListener(episodeItem: CarouselItem.EpisodeItem) {
            binding.playlistEpisodePlayButton.setOnClickListener {
                _clickPlayButtonEvent.tryEmit(
                    episodeItem
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CarouselItem.CharacterItem -> TYPE_CHARACTER
            is CarouselItem.LocationItem -> TYPE_LOCATION
            is CarouselItem.EpisodeItem -> TYPE_EPISODE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CHARACTER -> {
                CharacterViewHolder(
                    CharacterCarouselItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_LOCATION -> {
                LocationViewHolder(
                    LocationCarouselItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                EpisodeViewHolder(
                    EpisodeCarouselItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position).let {
                _clickEvent.tryEmit(
                    it
                )
            }
        }

        when (val item = getItem(position)) {
            is CarouselItem.CharacterItem -> {
                (holder as CharacterViewHolder).setCharacterDetails(item)
            }
            is CarouselItem.LocationItem -> {
                (holder as LocationViewHolder).setLocationDetails(item)
            }
            else -> {
                (holder as EpisodeViewHolder).setEpisodeDetails(item as CarouselItem.EpisodeItem)
                holder.setOnPlayButtonClickListener(item)
            }
        }
    }

    private fun TextView.setCarouselTextAutoSize() {
        setAutoSize(
            autoSizeMinTextSize = resources.getInteger(R.integer.carousel_auto_size_min_text_size),
            autoSizeMaxTextSize = resources.getInteger(R.integer.carousel_auto_size_max_text_size),
            maxLines = resources.getInteger(R.integer.carousel_text_max_lines)
        )
    }
}