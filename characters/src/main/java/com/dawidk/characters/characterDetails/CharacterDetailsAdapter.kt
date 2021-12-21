package com.dawidk.characters.characterDetails

import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.dawidk.characters.R
import com.dawidk.characters.databinding.CharacterDetailsInfoItemBinding
import com.dawidk.characters.databinding.CharacterDetailsTabsItemBinding
import com.dawidk.characters.databinding.EpisodeListItemBinding
import com.dawidk.characters.databinding.LocationListItemBinding
import com.dawidk.characters.model.*
import com.dawidk.characters.model.CharacterItem.*
import com.dawidk.characters.utils.capitalizeString
import com.dawidk.common.constants.EPISODE_IMAGE_SMALL
import com.dawidk.common.utils.*
import com.dawidk.core.domain.model.CharacterStatus
import com.dawidk.core.domain.model.Episode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private const val TYPE_INFO = 1
private const val TYPE_EPISODE = 2
private const val TYPE_LOCATION = 3
private const val TYPE_TAB = 4
private const val IMAGES_NUMBER = 4

class CharacterDetailsAdapter :
    ListAdapter<CharacterItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private val _onEpisodeClickEvent: MutableSharedFlow<Episode> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val onEpisodeClickEvent: SharedFlow<Episode> = _onEpisodeClickEvent
    private val _onTabClickEvent: MutableSharedFlow<String> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val onTabClickEvent: SharedFlow<String> = _onTabClickEvent
    private val _selectedTabEvent: MutableSharedFlow<String> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val selectedTabEvent: SharedFlow<String> = _selectedTabEvent

    private class ItemDiffCallback : DiffUtil.ItemCallback<CharacterItem>() {

        override fun areItemsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
            return when (oldItem) {
                is CharacterInfoItem -> oldItem == newItem
                is CharacterEpisodesItem -> oldItem == newItem
                is CharacterLocationItem -> oldItem == newItem
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: CharacterItem, newItem: CharacterItem): Boolean {
            return when (oldItem) {
                is CharacterInfoItem -> oldItem == newItem
                is CharacterEpisodesItem -> oldItem == newItem
                is CharacterLocationItem -> oldItem == newItem
                else -> oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CharacterInfoItem -> TYPE_INFO
            is CharacterEpisodesItem -> TYPE_EPISODE
            is CharacterLocationItem -> TYPE_LOCATION
            is CharacterTabItem -> TYPE_TAB
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_INFO -> {
                CharacterDetailsInfoViewHolder(
                    CharacterDetailsInfoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    parent,
                    parent.context.resources
                )
            }
            TYPE_EPISODE -> {
                CharacterDetailsEpisodeTabViewHolder(
                    EpisodeListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_LOCATION -> {
                CharacterDetailsLocationTabViewHolder(
                    LocationListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                CharacterDetailsTabViewHolder(
                    CharacterDetailsTabsItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    _onTabClickEvent,
                    _selectedTabEvent
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CharacterInfoItem -> {
                (holder as CharacterDetailsInfoViewHolder).applyItem(item)
            }
            is CharacterEpisodesItem -> {
                holder.itemView.apply {
                    setOnClickListener {
                        getItem(position)?.let { _onEpisodeClickEvent.tryEmit((it as CharacterEpisodesItem).episode) }
                    }
                    setMargins(
                        top = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        bottom = resources.getInteger(R.integer.tab_list_margin)
                            .toPx(resources),
                        start = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        end = resources.getInteger(R.integer.tab_list_margin).toPx(resources)
                    )
                }
                (holder as CharacterDetailsEpisodeTabViewHolder).applyItem(item)
            }
            is CharacterLocationItem -> {
                holder.itemView.apply {
                    setMargins(
                        top = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        bottom = resources.getInteger(R.integer.tab_list_margin)
                            .toPx(resources),
                        start = resources.getInteger(R.integer.tab_list_margin).toPx(resources),
                        end = resources.getInteger(R.integer.tab_list_margin).toPx(resources)
                    )
                }
                (holder as CharacterDetailsLocationTabViewHolder).applyItem(item)
            }
            is CharacterTabItem -> {
                (holder as CharacterDetailsTabViewHolder).applyItem()
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is CharacterDetailsEpisodeTabViewHolder -> {
                holder.scope.cancel()
            }
        }
    }

    internal class CharacterDetailsInfoViewHolder(
        private val binding: CharacterDetailsInfoItemBinding,
        private val parent: ViewGroup,
        private val resources: Resources
    ) : RecyclerView.ViewHolder(binding.root) {

        fun applyItem(characterInfoItem: CharacterInfoItem) {
            val (name, status, species, gender, image, episode, created) = characterInfoItem

            binding.apply {
                characterNameTextView.text = name

                characterImageView.apply {
                    load(image)
                    setPercentageSize(
                        parent = this@CharacterDetailsInfoViewHolder.parent,
                        percentageHeight = ResourcesCompat.getFloat(
                            resources,
                            R.dimen.character_details_image_height_percent
                        )
                    )
                }

                speciesTextView.text = species.capitalizeString()
                characterStatusTextView.text = status.value.capitalizeString()
                genderTextView.text = gender.capitalizeString()
                createdTextView.text =
                    created.substringBefore(resources.getString(R.string.created_date_delimiter))
                firstSeenTextView.text = episode.name
                val statusColor = when (status) {
                    CharacterStatus.ALIVE -> {
                        Color.GREEN
                    }
                    CharacterStatus.DEAD -> {
                        Color.RED
                    }
                    else -> {
                        Color.GRAY
                    }
                }

                statusCircleImageView.setColorFilter(statusColor, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    internal class CharacterDetailsEpisodeTabViewHolder(
        private val binding: EpisodeListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        lateinit var scope: CoroutineScope

        fun applyItem(characterEpisodesItem: CharacterEpisodesItem) {
            val (_, name, air_date, season, characters, _) = characterEpisodesItem.episode

            binding.apply {
                episodeName.apply {
                    text = name
                    setAutoSize(
                        autoSizeMinTextSize = resources.getInteger(R.integer.episode_name_auto_size_min_text_size),
                        autoSizeMaxTextSize = resources.getInteger(R.integer.episode_name_auto_size_max_text_size),
                        maxLines = resources.getInteger(R.integer.name_text_max_lines)
                    )
                }
                airDate.text = air_date
                episodeText.text = season

                scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    setEpisodeImage(
                        this@apply.episodeImageView,
                        this@apply.episodeImageView.context,
                        characters,
                        season,
                        IMAGES_NUMBER,
                        EPISODE_IMAGE_SMALL
                    )
                }
            }
        }
    }

    internal class CharacterDetailsLocationTabViewHolder(
        private val binding: LocationListItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun applyItem(characterLocationItem: CharacterLocationItem) {
            val (_, name, type, dimension, _, _) = characterLocationItem.location

            binding.apply {
                locationName.apply {
                    text = name
                    setAutoSize(
                        autoSizeMinTextSize = resources.getInteger(R.integer.location_name_auto_size_min_text_size),
                        autoSizeMaxTextSize = resources.getInteger(R.integer.location_name_auto_size_max_text_size),
                        maxLines = resources.getInteger(R.integer.name_text_max_lines)
                    )
                }
                typeText.text = type
                dimensionText.apply {
                    text = dimension.let {
                        if (it.isEmpty()) resources.getString(R.string.unknown)
                        else it
                    }
                }
                setLocationImage(
                    locationImageView,
                    characterLocationItem.location.id,
                    characterLocationItem.location.name
                )
            }
        }
    }

    internal class CharacterDetailsTabViewHolder(
        private val binding: CharacterDetailsTabsItemBinding,
        private val _onTabClickEvent: MutableSharedFlow<String>,
        private val _selectedTabEvent: MutableSharedFlow<String>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun applyItem() {
            _selectedTabEvent.tryEmit(
                binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.text.toString()
            )
            binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    _onTabClickEvent.tryEmit(tab.text.toString())
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }
}