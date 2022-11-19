package com.dawidk.home.playlists

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
import com.dawidk.home.databinding.SeeAllPlaylistItemsBinding
import com.dawidk.home.model.Playlist
import com.dawidk.home.model.PlaylistItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val CHARACTER_ITEM = 1
private const val LOCATION_ITEM = 2
private const val EPISODE_ITEM = 3
private const val SEE_ALL_ITEM = 4
private const val IMAGES_NUMBER = 4

class PlaylistAdapter(
    private val _playlistItemClickEvent: MutableSharedFlow<PlaylistItem>,
    private val _seeAllClickEvent: MutableSharedFlow<Playlist>,
    private val _playlistsPlayButtonClickEvent: MutableSharedFlow<PlaylistItem>
) :
    ListAdapter<PlaylistItem, RecyclerView.ViewHolder>(PlaylistDiffUtilCallback()), KoinComponent {

    var playListName: String = ""
    private val episodeProgressBarHandler: EpisodeProgressBarHandler by inject()

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
                    setSeeAllItemPercentageSize(it.root, parent)
                }
            )
            LOCATION_ITEM -> LocationItemViewHolder(
                LocationPlaylistItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(it.root, parent)
                }
            )
            EPISODE_ITEM -> EpisodeItemViewHolder(
                EpisodePlaylistItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(it.root, parent)
                }
            )
            SEE_ALL_ITEM -> SeeAllItemViewHolder(
                SeeAllPlaylistItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).also {
                    setSeeAllItemPercentageSize(it.root, parent)
                }
            )
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position).let {
                _playlistItemClickEvent.tryEmit(
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
            else -> {
                holder.itemView.setOnClickListener {
                    (getItem(position) as PlaylistItem.SeeAllItem).let {
                        _seeAllClickEvent.tryEmit(
                            Playlist(playListName, it.itemsList)
                        )
                    }
                }
                (holder as SeeAllItemViewHolder).applyItem()
            }
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

    private fun setSeeAllItemPercentageSize(view: View, parent: ViewGroup) {
        view.setPercentageSize(
            parent,
            percentageWidth = ResourcesCompat.getFloat(
                parent.context.resources,
                R.dimen.see_all_item_width_percent
            )
        )
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is EpisodeItemViewHolder -> {
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
                _playlistsPlayButtonClickEvent.tryEmit(
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
                    characterImage.context,
                    characterItem.image, CHARACTER_IMAGE_MEDIUM
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

class SeeAllItemViewHolder(binding: SeeAllPlaylistItemsBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun applyItem() {}
}
