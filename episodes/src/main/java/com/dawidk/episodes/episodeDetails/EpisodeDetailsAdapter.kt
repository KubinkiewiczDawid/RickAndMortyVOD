package com.dawidk.episodes.episodeDetails

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.dawidk.common.constants.CHARACTER_IMAGE_SMALL
import com.dawidk.common.episodeProgressBar.EpisodeProgressBarHandler
import com.dawidk.common.utils.*
import com.dawidk.core.domain.model.Character
import com.dawidk.episodes.R
import com.dawidk.episodes.databinding.EpisodeDetailsCharacterItemBinding
import com.dawidk.episodes.databinding.EpisodeDetailsInfoItemBinding
import com.dawidk.episodes.databinding.EpisodeDetailsTabItemBinding
import com.dawidk.episodes.model.EpisodeItem
import com.dawidk.episodes.model.EpisodeItem.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TYPE_INFO = 1
private const val TYPE_TAB = 2
private const val TYPE_CHARACTER = 3
private const val IMAGES_NUMBER = 4

class EpisodeDetailsAdapter(private val episodeId: String) :
    ListAdapter<EpisodeItem, RecyclerView.ViewHolder>(EpisodeDiffUtilCallback()), KoinComponent {

    private val episodeProgressBarHandler: EpisodeProgressBarHandler by inject()
    private val _clickEvent: MutableSharedFlow<Character> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clickEvent: SharedFlow<Character> = _clickEvent
    private val _episodePlayClickEvent: MutableSharedFlow<Unit> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val episodePlayClickEvent: SharedFlow<Unit> = _episodePlayClickEvent

    private class EpisodeDiffUtilCallback : DiffUtil.ItemCallback<EpisodeItem>() {

        override fun areItemsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean =
            oldItem == newItem
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EpisodeInfoItem -> TYPE_INFO
            is EpisodeTabItem -> TYPE_TAB
            is EpisodeCharacterItem -> TYPE_CHARACTER
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INFO -> {
                EpisodeDetailsInfoViewHolder(
                    EpisodeDetailsInfoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    _episodePlayClickEvent,
                    parent,
                    parent.context.resources
                )
            }
            TYPE_TAB -> {
                EpisodeDetailsTabViewHolder(
                    EpisodeDetailsTabItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                EpisodeDetailsCharactersViewHolder(
                    EpisodeDetailsCharacterItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is EpisodeInfoItem -> {
                (holder as EpisodeDetailsInfoViewHolder).applyItem(item)
            }
            is EpisodeTabItem -> {
            }
            is EpisodeCharacterItem -> {
                holder.itemView.apply {
                    setOnClickListener {
                        getItem(position)?.let {
                            _clickEvent.tryEmit((it as EpisodeCharacterItem).character)
                        }
                    }
                    setMargins(
                        top = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        bottom = resources.getInteger(R.integer.tab_list_margin)
                            .toPx(resources),
                        start = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        end = resources.getInteger(R.integer.tab_list_margin).toPx(resources)
                    )
                }
                (holder as EpisodeDetailsCharactersViewHolder).applyItem(item)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is EpisodeDetailsInfoViewHolder -> {
                holder.scope.cancel()
            }
            is EpisodeDetailsCharactersViewHolder -> {
                holder.scope.cancel()
            }
        }
    }

    inner class EpisodeDetailsInfoViewHolder(
        private val binding: EpisodeDetailsInfoItemBinding,
        private val _episodePlayClickEvent: MutableSharedFlow<Unit>,
        private val parent: ViewGroup,
        private val resources: Resources
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var scope: CoroutineScope

        fun applyItem(episodeInfoItem: EpisodeInfoItem) {
            val (name, air_date, characters, season) = episodeInfoItem
            binding.apply {
                episodeNameText.text = name
                episodeAirDateText.text = air_date
                episodeSeasonText.text = season

                episodeDetailsPlayButton.setOnClickListener {
                    _episodePlayClickEvent.tryEmit(Unit)
                }

                episodeImageView.setPercentageSize(
                    parent = parent,
                    percentageHeight = ResourcesCompat.getFloat(
                        resources,
                        R.dimen.episode_details_image_height_percent
                    )
                )
                val memoryCacheKey =
                    root.context?.imageLoader?.memoryCache?.get(MemoryCache.Key(season))

                scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    async {
                        if (memoryCacheKey != null) {
                            episodeImageView.load(memoryCacheKey) {
                                memoryCachePolicy(CachePolicy.ENABLED)
                            }
                        } else {
                            setEpisodeImage(
                                episodeImageView,
                                episodeImageView.context,
                                characters,
                                season,
                                IMAGES_NUMBER
                            )
                        }
                    }
                    async {
                        episodeProgressBarHandler.setProgressBar(
                            binding.episodeProgressBar,
                            episodeId
                        )
                    }
                }
            }
        }
    }

    inner class EpisodeDetailsTabViewHolder(
        binding: EpisodeDetailsTabItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {}

    inner class EpisodeDetailsCharactersViewHolder(
        private val binding: EpisodeDetailsCharacterItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var scope: CoroutineScope

        fun applyItem(episodeCharacterItem: EpisodeCharacterItem) {
            binding.apply {
                nameTv.text = episodeCharacterItem.character.name
                scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    val image = fetchBitmapImage(
                        characterIv.context,
                        episodeCharacterItem.character.image, CHARACTER_IMAGE_SMALL
                    )
                    characterIv.load(image)
                }
            }
        }
    }
}

