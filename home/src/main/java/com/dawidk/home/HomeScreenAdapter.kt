package com.dawidk.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dawidk.common.utils.setPercentageSize
import com.dawidk.home.carousel.CarouselAdapter
import com.dawidk.home.databinding.CarouselViewpagerItemsBinding
import com.dawidk.home.databinding.PlaylistsRecyclerviewItemsBinding
import com.dawidk.home.model.*
import com.dawidk.home.playlists.PlaylistAdapter
import com.dawidk.home.utils.setUpInfinityScroll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

private const val TYPE_PLAYLIST = 1
private const val TYPE_CAROUSEL_ITEM = 2

class HomeScreenAdapter : ListAdapter<HomeItem, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    private val _seeAllClickEvent: MutableSharedFlow<Playlist> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val seeAllClickEvent: SharedFlow<Playlist> = _seeAllClickEvent
    private var _playlistItemClickEvent: MutableSharedFlow<PlaylistItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val playlistItemClickEvent: SharedFlow<PlaylistItem> = _playlistItemClickEvent
    private val _carouselClickEvent: MutableSharedFlow<CarouselItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val carouselClickEvent: SharedFlow<CarouselItem> = _carouselClickEvent
    private val _carouselPlayButtonClickEvent: MutableSharedFlow<CarouselItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val carouselPlayButtonClickEvent: SharedFlow<CarouselItem> = _carouselPlayButtonClickEvent
    private val _playlistPlayButtonClickEvent: MutableSharedFlow<PlaylistItem> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val playlistPlayButtonClickEvent: SharedFlow<PlaylistItem> = _playlistPlayButtonClickEvent

    private class ItemDiffCallback : DiffUtil.ItemCallback<HomeItem>() {

        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return when (oldItem) {
                is Playlist -> oldItem == newItem
                is CarouselItems -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return when (oldItem) {
                is Playlist -> oldItem.items == (newItem as Playlist).items
                is CarouselItems -> oldItem.items == (newItem as CarouselItems).items
            }
        }
    }

    inner class PlaylistsViewHolder(
        binding: PlaylistsRecyclerviewItemsBinding,
        private val context: Context,
        parent: ViewGroup
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private val playlistName: TextView = binding.playlistName
        private val playlistAdapter = PlaylistAdapter(
            _playlistItemClickEvent,
            _seeAllClickEvent,
            _playlistPlayButtonClickEvent
        )
        private val recyclerView: RecyclerView = binding.playlistRecyclerView

        fun applyItem(item: Playlist) {
            playlistName.apply {
                val param = layoutParams as ViewGroup.MarginLayoutParams
                param.topMargin = context.resources.getInteger(R.integer.home_playlists_row_padding)
                layoutParams = param
                text = item.name
            }
            recyclerView.apply {
                adapter = playlistAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                setRecycledViewPool(viewPool)
                (layoutManager as LinearLayoutManager).initialPrefetchItemCount = 3
            }
            playlistAdapter.playListName = item.name
            playlistAdapter.submitList(item.items)
        }
    }

    inner class CarouselViewHolder(private val binding: CarouselViewpagerItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val carouselAdapter =
            CarouselAdapter(_carouselClickEvent, _carouselPlayButtonClickEvent)
        private val carouselViewPager: ViewPager2 = binding.carouselViewPager

        @SuppressLint("ResourceType")
        fun applyItem(item: CarouselItems) {
            carouselViewPager.apply {
                adapter = carouselAdapter
                setUpInfinityScroll {
                    carouselAdapter.getOriginalItemCount()
                }
            }
            carouselAdapter.submitList(item.items)
            carouselViewPager.offscreenPageLimit = item.items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Playlist -> TYPE_PLAYLIST
            is CarouselItems -> TYPE_CAROUSEL_ITEM
            else -> throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PLAYLIST -> {
                PlaylistsViewHolder(
                    PlaylistsRecyclerviewItemsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    parent.context,
                    parent
                ).also {
                    it.itemView.rootView.setPercentageSize(
                        parent,
                        ResourcesCompat.getFloat(
                            parent.context.resources,
                            R.dimen.playlist_item_height_percent
                        )
                    )
                }
            }
            else -> {
                CarouselViewHolder(
                    CarouselViewpagerItemsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).also {
                    it.itemView.rootView.setPercentageSize(
                        parent,
                        ResourcesCompat.getFloat(
                            parent.context.resources,
                            R.dimen.carousel_item_height_percent
                        )
                    )
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Playlist -> {
                (holder as PlaylistsViewHolder).applyItem(item)
            }
            is CarouselItems -> {
                (holder as CarouselViewHolder).applyItem(item)
            }
        }
    }
}