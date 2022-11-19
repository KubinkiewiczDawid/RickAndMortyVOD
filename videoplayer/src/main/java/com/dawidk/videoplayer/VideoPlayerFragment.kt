package com.dawidk.videoplayer

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.video.VideoType
import com.dawidk.core.utils.DataLoadingException
import com.dawidk.videoplayer.cast.ExpandedControlsActivity
import com.dawidk.videoplayer.databinding.FragmentVideoPlayerBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VideoPlayerFragment : Fragment(R.layout.fragment_video_player), ErrorDialogFragment.Callback {

    private val viewModel by viewModel<VideoPlayerViewModel>()
    private val binding by viewBinding(FragmentVideoPlayerBinding::bind)
    private val networkMonitor: NetworkMonitor by inject()
    private lateinit var videoId: String
    private lateinit var videoType: VideoType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = VideoPlayerFragmentArgs.fromBundle(requireActivity().intent.extras!!)
        videoId = args.id
        videoType = args.videoType

        registerStateListener()
        lifecycleScope.launch { checkInternetConnection() }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAction(VideoPlayerAction.FillVideoData(videoId, videoType))
        viewModel.onAction(VideoPlayerAction.StartVideoPlayer(binding.videoPlayerView))
        setScreenMode(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
    }

    override fun onStop() {
        super.onStop()
        binding.videoPlayerView.player = null
        setScreenMode(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        val minimumHeight =
            if (orientation == Configuration.ORIENTATION_PORTRAIT) 0 else ViewGroup.LayoutParams.MATCH_PARENT
        val isVideoDescriptionVisible = (orientation == Configuration.ORIENTATION_PORTRAIT)
        val playerConstraintPercentageHeight = if (orientation == Configuration.ORIENTATION_PORTRAIT) 0.4f else 1.0f
        val videoPlayerLayout = binding.videoPlayerLayout
        val set = ConstraintSet()
        set.clone(videoPlayerLayout)
        set.constrainPercentHeight(binding.videoPlayerView.id, playerConstraintPercentageHeight)
        set.applyTo(videoPlayerLayout)
        binding.apply {
            videoPlayerView.minimumHeight = minimumHeight
            videoNameTextView.isVisible = isVideoDescriptionVisible
            videoDetailsTextView.isVisible = isVideoDescriptionVisible
            videoMoreDetailsTextView.isVisible = isVideoDescriptionVisible
            videoDescriptionTextView.isVisible = isVideoDescriptionVisible
            videoDetailsSeparator.isVisible = isVideoDescriptionVisible
        }
        viewModel.onAction(VideoPlayerAction.FillVideoData(videoId, videoType))
    }

    private fun registerStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    when (it) {
                        is VideoPlayerState.Loading -> showLoading()
                        is VideoPlayerState.PlayerSet -> {
                            binding.videoPlayerView.player = it.player
                        }
                        is VideoPlayerState.FillVideoDataSuccess -> updateUI(it)
                        is VideoPlayerState.Error -> showError(it)
                        is VideoPlayerState.OpenExpandedCastControls -> {
                            openExpandedCastControls()
                        }
                    }
                }
            }
        }
    }

    private fun openExpandedCastControls() {
        hideLoading()
        findNavController().popBackStack()
        val intent =
            Intent(requireActivity(), ExpandedControlsActivity::class.java)
        startActivity(intent)
    }

    private suspend fun checkInternetConnection(): Boolean {
        var isConnected = true
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkMonitor.state.collect {
                    when (it) {
                        true -> isConnected = true
                        false -> {
                            ErrorDialogFragment.show(
                                childFragmentManager,
                                Throwable(getString(R.string.no_internet_error_message))
                            )
                            isConnected = false
                        }
                    }
                }
            }
        }.join()
        return isConnected
    }

    private fun setScreenMode(screenOrientation: Int) {
        requireActivity().requestedOrientation = screenOrientation
    }

    private fun showError(state: VideoPlayerState.Error) {
        hideLoading()
        ErrorDialogFragment.show(childFragmentManager, state.exception)
    }

    private fun updateUI(state: VideoPlayerState.FillVideoDataSuccess) {
        hideLoading()
        val (name, details, moreDetails, description) = state.video
        binding.apply {
            videoNameTextView.text = name
            videoDetailsTextView.text = details
            videoMoreDetailsTextView.text = moreDetails
            videoDescriptionTextView.text =
                description ?: resources.getString(R.string.lorem_ipsum)
        }
    }

    private fun showLoading() {
        binding.videoPlayerProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.videoPlayerProgressBar.isVisible = false
    }

    override fun onPositiveButtonClicked(error: Throwable) {
        if (error is DataLoadingException) {
            viewModel.onAction(VideoPlayerAction.FillVideoData(videoId, videoType))
        } else {
            lifecycleScope.launch {
                val isConnected = checkInternetConnection()
                if (isConnected) {
                    viewModel.onAction(VideoPlayerAction.StartVideoPlayer(binding.videoPlayerView))
                }
            }
        }
    }

    override fun onNegativeButtonClicked() {
        requireActivity().finish()
    }
}