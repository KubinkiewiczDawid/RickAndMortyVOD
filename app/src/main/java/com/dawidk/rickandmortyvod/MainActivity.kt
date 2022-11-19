package com.dawidk.rickandmortyvod

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationBarView
import com.dawidk.rickandmortyvod.databinding.ActivityMainBinding
import com.dawidk.rickandmortyvod.navigation.MainNavigator
import com.dawidk.rickandmortyvod.navigation.Screen
import com.dawidk.rickandmortyvod.state.MainAction
import com.dawidk.rickandmortyvod.state.MainEvent
import com.dawidk.rickandmortyvod.utils.createUpdateServiceIntervalAlarm
import com.dawidk.rickandmortyvod.utils.fitSystemWindow
import com.dawidk.common.navigation.BottomNavigationHandler
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.setMargins
import com.dawidk.common.utils.toPx
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val TOOLBAR_HEIGHT_FLOAT = 80F

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationBarView.OnItemSelectedListener {

    private lateinit var navController: NavController
    private val viewModel by viewModel<MainViewModel>()
    private val mainNavigator: MainNavigator by inject()
    private val networkMonitor: NetworkMonitor by inject()
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(networkMonitor)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, binding.mediaRouteButton)

        this.createUpdateServiceIntervalAlarm()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
        binding.bottomNavigationView.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener(this@MainActivity)
        }
        mainNavigator.navController = navController

        registerMenuItemClickListener()
        registerEventListener()
        registerBottomNavigationHandler()
    }

    private fun registerMenuItemClickListener() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings -> {
                    viewModel.onAction(MainAction.NavigateToSettingsScreen)
                    true
                }
                else -> false
            }
        }
    }

    private fun registerEventListener() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is MainEvent.NavigateToSettingsScreen -> navigateToSettingsScreen()
                    }
                }
            }
        }
    }

    private fun registerBottomNavigationHandler() {
        bottomNavigationHandler = BottomNavigationHandler(
            navController = this@MainActivity.navController,
            primaryFragmentId = R.id.homeFragment,
            primaryNavBarPositionId = binding.bottomNavigationView.menu.getItem(0).itemId
        )
        this.onBackPressedDispatcher.addCallback(this) {
            bottomNavigationHandler.handleBackButton(
                changeTab = {
                    binding.bottomNavigationView.selectedItemId = it
                },
                applicationFinish = {
                    finish()
                }
            )
        }
    }

    private fun navigateToSettingsScreen() {
        mainNavigator.navigateTo(Screen.SettingsScreen)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                setupContent(showToolbar = true, showBottomBar = true, fitToSystemWindow = false)
                binding.navHostFragment.setMargins()
            }
            R.id.settingsFragment -> {
                setupContent(showToolbar = false, showBottomBar = false, fitToSystemWindow = true)
                binding.navHostFragment.setMargins()
            }
            R.id.videoPlayerFragment -> {
                setupContent(
                    showToolbar = true,
                    showBottomBar = false,
                    fitToSystemWindow = false,
                    stillToolbar = true
                )
            }
            R.id.episodesFragment,
            R.id.episodeDetailsFragment -> {
                setupContent(
                    showToolbar = true,
                    showBottomBar = true,
                    fitToSystemWindow = false,
                    stillToolbar = true
                )
            }
            else -> {
                setupContent(showToolbar = false, showBottomBar = true, fitToSystemWindow = true)
                binding.navHostFragment.setMargins()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        bottomNavigationHandler.handleDestinationTabChange(item.itemId)
        onNavDestinationSelected(item, navController)
        return true
    }

    private fun setupContent(
        showToolbar: Boolean = true,
        showBottomBar: Boolean = true,
        fitToSystemWindow: Boolean = true,
        stillToolbar: Boolean = false
    ) {
        binding.toolbar.isGone = !showToolbar
        binding.bottomNavigationView.isGone = !showBottomBar
        window.fitSystemWindow(fitToSystemWindow)
        setStillToolbar(stillToolbar)
    }

    private fun enableScroll() {
        val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = (
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                )
        binding.toolbar.layoutParams = params
    }

    private fun setStillToolbar(isStill: Boolean) {
        if (isStill) {
            binding.navHostFragment.setMargins(top = TOOLBAR_HEIGHT_FLOAT.toPx(resources))
            binding.topAppBar.setExpanded(true, false)
            disableScroll()
        } else
            enableScroll()
    }

    private fun disableScroll() {
        val params = binding.toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        binding.toolbar.layoutParams = params
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}