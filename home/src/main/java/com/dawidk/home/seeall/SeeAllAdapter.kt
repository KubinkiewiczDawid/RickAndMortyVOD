package com.dawidk.home.seeall

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
import com.dawidk.common.episodeProgressBar.EpisodeProgressBarHandler
import com.dawidk.common.utils.fetchBitmapImage
import com.dawidk.common.utils.setEpisodeImage
import com.dawidk.common.utils.setLocationImage
import com.dawidk.common.utils.setPercentageSize
import com.dawidk.home.R
import com.dawidk.home.databinding.CharacterPlaylistItemsBinding
import com.dawidk.home.databinding.EpisodePlaylistItemsBinding
import com.dawidk.home.databinding.LocationPlaylistItemsBinding
import com.dawidk.home.model.PlaylistItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val CHARACTER_ITEM = 1
private const val LOCATION_ITEM = 2
private const val EPISODE_ITEM = 3
private const val SEE_ALL_ITEM = 4
private const val IMAGES_NUMBER = 4

class SeeAllAdapter :
    ListAdapter<PlaylistItem, RecyclerView.ViewHolder>(PlaylistDiffUtilCallback()), KoinComponent {

    private val episodeProgressBarHandler: EpisodeProgressBarHandler by inject()
    private val _itemClickEvent: MutableSharedFlow<PlaylistItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val itemClickEvent: SharedFlow<PlaylistItem> = _itemClickEvent
    private val _playButtonClickEvent: MutableSharedFlow<PlaylistItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val playButtonClickEvent: SharedFlow<PlaylistItem> = _playButtonClickEvent

    private class PlaylistDiffUtilCallback : DiffUtil.ItemCallback<PlaylistItem>() {

        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHARACTER_ITEM -> CharacterItemViewHolder(
                CharacterPlaylistItemsBinding.inflate(
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
                LocationPlaylistItemsBinding.inflate(
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
                EpisodePlaylistItemsBinding.inflate(
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
            getItem(position).let {
                _itemClickEvent.tryEmit(
                    it
                )
            }
        }
        when (getItemViewType(position)) {
            CHARACTER_ITEM -> (holder as CharacterItemViewHolder).applyItem(
                getItem(position) as PlaylistItem.CharacterItem
            )
            LOCATION_ITEM -> (holder as LocationItemViewHolder).applyItem(
                getItem(position) as PlaylistItem.LocationItem
            )
            EPISODE_ITEM -> {
                (holder as EpisodeItemViewHolder).applyItem(
                    getItem(position) as PlaylistItem.EpisodeItem
                )
                holder.setOnPlayButtonClickListener(getItem(position) as PlaylistItem.EpisodeItem)
            }
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PlaylistItem.CharacterItem -> CHARACTER_ITEM
            is PlaylistItem.LocationItem -> LOCATION_ITEM
            is PlaylistItem.EpisodeItem -> EPISODE_ITEM
            is PlaylistItem.SeeAllItem -> SEE_ALL_ITEM
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
                R.dimen.see_all_item_height_percent
            ) / 2
        } else {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.see_all_item_height_percent
            )
        }
    }

    private fun getWidth(parent: ViewGroup, isTablet: Boolean): Float {
        return if (isTablet) {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.see_all_item_width_percent
            ) / 2
        } else {
            ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.see_all_item_width_percent
            )
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is EpisodeItemViewHolder -> {
                holder.scope.cancel()
            }
            is CharacterItemViewHolder -> {
                holder.scope.cancel()
            }
        }
    }

    inner class EpisodeItemViewHolder(private val binding: EpisodePlaylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val episodeName: TextView = binding.episodePlaylistItemName
        private val episodeImage: ImageView = binding.episodePlaylistItemImage
        lateinit var scope: CoroutineScope

        fun applyItem(episodeItem: PlaylistItem.EpisodeItem) {
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
                episodeProgressBarHandler.setProgressBar(
                    binding.playlistEpisodeProgressBar,
                    episodeItem.id,
                )
            }
        }

        fun setOnPlayButtonClickListener(episodeItem: PlaylistItem.EpisodeItem) {
            binding.playlistEpisodePlayButton.setOnClickListener {
                _playButtonClickEvent.tryEmit(
                    episodeItem
                )
            }
        }
    }

    inner class CharacterItemViewHolder(binding: CharacterPlaylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val characterName: TextView = binding.playlistItemName
        private val characterImage = binding.playlistItemImage
        lateinit var scope: CoroutineScope

        fun applyItem(characterItem: PlaylistItem.CharacterItem) {
            scope = CoroutineScope(Dispatchers.Main)
            characterName.apply {
                text = characterItem.name
            }

            scope.launch {
                val image = fetchBitmapImage(
                    characterImage.context, characterItem.image,
                    CHARACTER_IMAGE_MEDIUM
                )
                characterImage.load(image)
            }
        }
    }
}

class LocationItemViewHolder(binding: LocationPlaylistItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val locationName: TextView = binding.locationPlaylistItemName
    private val locationImage: ImageView = binding.locationPlaylistItemImage

    fun applyItem(locationItem: PlaylistItem.LocationItem) {
        locationName.apply {
            text = locationItem.name
        }
        setLocationImage(locationImage, locationItem.id, locationItem.name)
    }
}