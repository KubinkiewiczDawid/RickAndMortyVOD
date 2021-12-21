package com.dawidk.search.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dawidk.common.constants.CHARACTER_IMAGE_MEDIUM
import com.dawidk.common.constants.EPISODE_IMAGE_MEDIUM
import com.dawidk.common.utils.fetchBitmapImage
import com.dawidk.common.utils.setEpisodeImage
import com.dawidk.common.utils.setLocationImage
import com.dawidk.common.utils.setPercentageSize
import com.dawidk.search.R
import com.dawidk.search.databinding.CharacterSearchItemsBinding
import com.dawidk.search.databinding.EpisodeSearchItemsBinding
import com.dawidk.search.databinding.LocationSearchItemsBinding
import com.dawidk.search.model.SearchItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private const val CHARACTER_ITEM = 1
private const val LOCATION_ITEM = 2
private const val EPISODE_ITEM = 3
private const val IMAGES_NUMBER = 4

class SearchAdapter :
    ListAdapter<SearchItem, RecyclerView.ViewHolder>(PlaylistDiffUtilCallback()) {

    private val _clickEvent: MutableSharedFlow<SearchItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clickEvent: SharedFlow<SearchItem> = _clickEvent

    private class PlaylistDiffUtilCallback : DiffUtil.ItemCallback<SearchItem>() {

        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHARACTER_ITEM -> CharacterItemViewHolder(
                CharacterSearchItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(
                        it.root,
                        parent,
                        parent.context.resources.getBoolean(R.bool.isTablet)
                    )
                }
            )
            LOCATION_ITEM -> LocationItemViewHolder(
                LocationSearchItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(
                        it.root,
                        parent,
                        parent.context.resources.getBoolean(R.bool.isTablet)
                    )
                }
            )
            EPISODE_ITEM -> EpisodeItemViewHolder(
                EpisodeSearchItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(
                        it.root,
                        parent,
                        parent.context.resources.getBoolean(R.bool.isTablet)
                    )
                }
            )
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position)?.let { _clickEvent.tryEmit(it) }
        }

        when (getItemViewType(position)) {
            CHARACTER_ITEM -> (holder as CharacterItemViewHolder).applyItem(
                getItem(position) as SearchItem.CharacterItem
            )
            LOCATION_ITEM -> (holder as LocationItemViewHolder).applyItem(
                getItem(position) as SearchItem.LocationItem
            )
            else -> (holder as EpisodeItemViewHolder).applyItem(
                getItem(position) as SearchItem.EpisodeItem
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchItem.CharacterItem -> CHARACTER_ITEM
            is SearchItem.LocationItem -> LOCATION_ITEM
            is SearchItem.EpisodeItem -> EPISODE_ITEM
        }
    }

    private fun setSeeAllItemPercentageSize(view: View, parent: ViewGroup, isTablet: Boolean) {
        view.setPercentageSize(
            parent,
            getHeight(parent, isTablet),
            getWidth(parent, isTablet)
        )
    }

    private fun getHeight(parent: ViewGroup, isTablet: Boolean): Float {
        return if (isTablet) {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.search_item_height_percent
            ) / 2
        } else {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.search_item_height_percent
            )
        }
    }

    private fun getWidth(parent: ViewGroup, isTablet: Boolean): Float {
        return if (isTablet) {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.search_item_width_percent
            ) / 2
        } else {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.search_item_width_percent
            )
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is CharacterItemViewHolder -> {
                holder.scope.cancel()
            }
            is EpisodeItemViewHolder -> {
                holder.scope.cancel()
            }
        }
    }

    inner class CharacterItemViewHolder(binding: CharacterSearchItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val characterName: TextView = binding.playlistItemName
        private val characterImage = binding.playlistItemImage
        lateinit var scope: CoroutineScope

        fun applyItem(characterItem: SearchItem.CharacterItem) {
            characterName.apply {
                text = characterItem.name
            }
            scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val image = fetchBitmapImage(
                    characterImage.context,
                    characterItem.image, CHARACTER_IMAGE_MEDIUM
                )
                characterImage.load(image)
            }
        }
    }
}

class LocationItemViewHolder(binding: LocationSearchItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val locationName: TextView = binding.locationPlaylistItemName
    private val locationImage: ImageView = binding.locationPlaylistItemImage

    fun applyItem(locationItem: SearchItem.LocationItem) {
        locationName.apply {
            text = locationItem.name
        }
        setLocationImage(locationImage, locationItem.id, locationItem.name)
    }
}

class EpisodeItemViewHolder(binding: EpisodeSearchItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val episodeName: TextView = binding.episodePlaylistItemName
    private val episodeImage: ImageView = binding.episodePlaylistItemImage
    lateinit var scope: CoroutineScope

    fun applyItem(episodeItem: SearchItem.EpisodeItem) {
        episodeName.apply {
            text = "${episodeItem.name} (${episodeItem.episode})"
        }
        scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            setEpisodeImage(
                episodeImage,
                episodeImage.context,
                episodeItem.characters,
                episodeItem.episode,
                IMAGES_NUMBER,
                EPISODE_IMAGE_MEDIUM
            )
        }
    }
}