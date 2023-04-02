package com.dawidk.home.seeall

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.utils.collectFromState
import com.dawidk.common.utils.isTablet
import com.dawidk.home.R
import com.dawidk.home.databinding.SeeAllFragmentBinding
import com.dawidk.home.model.PlaylistItem
import com.dawidk.home.navigation.HomeNavigator
import com.dawidk.home.navigation.Screen
import com.dawidk.videoplayer.cast.service.CastVideoService
import org.koin.android.ext.android.inject

class SeeAllFragment : Fragment(R.layout.see_all_fragment) {

    private val binding by viewBinding(SeeAllFragmentBinding::bind)
    private val args: SeeAllFragmentArgs by navArgs()
    private val homeNavigator: HomeNavigator by inject()
    private var seeAllAdapter: SeeAllAdapter? = null
    private val castVideoService: CastVideoService by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seeAllAdapter = SeeAllAdapter()
        homeNavigator.navController = findNavController()

        setUpRecyclerView()
        registerClickEventListener()
        setUpUi()
    }

    override fun onDestroyView() {
        seeAllAdapter = null
        super.onDestroyView()
    }

    private fun setUpUi() {
        binding.tvPlaylistName.text = args.playList.name
        seeAllAdapter?.submitList(args.playList.items)
    }

    private fun setUpRecyclerView() {
        binding.rvPlaylistItems.apply {
            adapter = seeAllAdapter
            layoutManager = GridLayoutManager(
                activity,
                if (context.isTablet()) {
                    resources.getInteger(R.integer.grid_layout_tablet_span_count)
                } else {
                    resources.getInteger(R.integer.grid_layout_span_count)
                }
            )
        }
    }

    private fun registerClickEventListener() {
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, seeAllAdapter?.itemClickEvent) {
            when (it) {
                is PlaylistItem.CharacterItem -> {
                    homeNavigator.navigateTo(Screen.CharacterDetails(it.id))
                }
                is PlaylistItem.EpisodeItem -> {
                    homeNavigator.navigateTo(Screen.EpisodeDetails(it.id))
                }
                else -> {}
            }
        }
        viewLifecycleOwner.collectFromState(Lifecycle.State.STARTED, seeAllAdapter?.playButtonClickEvent) {
            when (it) {
                is PlaylistItem.EpisodeItem -> {
                    if (it.id != castVideoService.castedVideoId) {
                        homeNavigator.navigateTo(Screen.VideoPlayer(it.id))
                    }
                }
                else -> {}
            }
        }
    }
}