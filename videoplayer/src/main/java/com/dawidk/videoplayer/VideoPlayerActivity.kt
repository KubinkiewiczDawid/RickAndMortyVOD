package com.dawidk.videoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dawidk.videoplayer.databinding.ActivityVideoPlayerBinding

private const val EPISODE_ID_KEY = "id"
private const val EPISODE_VIDEO_TYPE_KEY = "videoType"

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityVideoPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.videoplayer_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val id = intent.getStringExtra(EPISODE_ID_KEY)
        val videoType = intent.getSerializableExtra(EPISODE_VIDEO_TYPE_KEY)
        navHostFragment.arguments = bundleOf(Pair(EPISODE_ID_KEY, id), Pair(EPISODE_VIDEO_TYPE_KEY, videoType))
    }
}