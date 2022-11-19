package com.dawidk.episodes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dawidk.common.constants.EPISODE_IMAGE_MEDIUM
import com.dawidk.common.episodeProgressBar.EpisodeProgressBarHandler
import com.dawidk.common.utils.setEpisodeImage
import com.dawidk.core.domain.model.Episode
import com.dawidk.episodes.databinding.EpisodeItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val IMAGES_NUMBER = 4

class EpisodesAdapter :
    ListAdapter<Episode, EpisodesAdapter.EpisodeViewHolder>(EpisodeDiffUtilCallback()),
    KoinComponent {

    private val _clickEvent: MutableSharedFlow<Episode> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clickEvent: SharedFlow<Episode> = _clickEvent
    private val _playButtonClickEvent: MutableSharedFlow<Episode> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val playButtonClickEvent: SharedFlow<Episode> = _playButtonClickEvent
    private val episodeProgressBarHandler: EpisodeProgressBarHandler by inject()

    private class EpisodeDiffUtilCallback : DiffUtil.ItemCallback<Episode>() {

        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            EpisodeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            getItem(position)?.let { _clickEvent.tryEmit(it) }
        }
        holder.setOnPlayButtonClickListener(getItem(position))
        getItem(position)?.let { holder.applyItem(it) }
    }

    override fun onViewRecycled(holder: EpisodeViewHolder) {
        super.onViewRecycled(holder)
        holder.episodeImage.load(R.drawable.placeholder_img)
        holder.scope.cancel()
    }

    inner class EpisodeViewHolder(private val binding: EpisodeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val episodeName: TextView = binding.episodeNameTextView
        private val episodeSeason: TextView = binding.seasonNameLayout.seasonTextView
        private val airDate: TextView = binding.airDateLayout.dateTextView
        val episodeImage: ImageView = binding.episodeImageView
        lateinit var episode: Episode
        lateinit var scope: CoroutineScope

        fun applyItem(episode: Episode) {
            this.episode = episode
            episodeName.apply {
                text = episode.name
            }
            episodeSeason.text = episode.episode
            airDate.text = episode.airDate
            scope = CoroutineScope(Dispatchers.Main)

            scope.launch {
                setEpisodeImage(
                    episodeImage,
                    episodeImage.context,
                    episode.characters,
                    episode.episode,
                    IMAGES_NUMBER,
                    EPISODE_IMAGE_MEDIUM
                )
                episodeProgressBarHandler.setProgressBar(
                    binding.episodeItemProgressBar,
                    episode.id
                )
            }

        }

        fun setOnPlayButtonClickListener(episode: Episode) {
            binding.episodesListPlayButton.setOnClickListener {
                _playButtonClickEvent.tryEmit(
                    episode
                )
            }
        }
    }
}

