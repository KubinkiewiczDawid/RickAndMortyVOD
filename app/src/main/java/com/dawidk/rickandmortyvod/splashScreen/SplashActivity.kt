package com.dawidk.rickandmortyvod.splashScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.dawidk.common.errorHandling.ErrorDialogFragment
import com.dawidk.common.utils.NetworkMonitor
import com.dawidk.common.utils.collectFromState
import com.dawidk.core.domain.model.AccountInfo
import com.dawidk.registration.RegistrationActivity
import com.dawidk.rickandmortyvod.ACCOUNT_INFO_KEY
import com.dawidk.rickandmortyvod.MainActivity
import com.dawidk.rickandmortyvod.databinding.ActivitySplashBinding
import com.dawidk.rickandmortyvod.splashScreen.state.SplashAction
import com.dawidk.rickandmortyvod.splashScreen.state.SplashEvent
import com.dawidk.rickandmortyvod.splashScreen.state.SplashState
import com.dawidk.videoplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ANIMATION_DURATION: Long = 3000
private const val DEEP_LINK_KEY = "deepLink"

class SplashActivity : AppCompatActivity(),
    ErrorDialogFragment.Callback {

    private var activityScope = CoroutineScope(Dispatchers.Main)
    private lateinit var binding: ActivitySplashBinding
    private val networkMonitor: NetworkMonitor by inject()
    private val viewModel by viewModel<SplashViewModel>()
    private var isUserSignedIn: Boolean = false
    private var intentData: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        val deepLink = extras?.getString(DEEP_LINK_KEY)
        intentData = if (deepLink != null) {
            Uri.parse(deepLink)
        } else {
            intent.data
        }

        viewModel.onAction(
            SplashAction.Init(
                intent.getParcelableExtra(ACCOUNT_INFO_KEY) ?: AccountInfo.EMPTY
            )
        )

        lifecycle.addObserver(networkMonitor)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rotateAnimation = RotateAnimation(
            0F, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = ANIMATION_DURATION
        rotateAnimation.repeatCount = Animation.INFINITE

        binding.rickAndMortyLogo.startAnimation(rotateAnimation)
        registerStateListener()
        registerEventListener()
        checkInternetConnection()
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }

    private fun registerStateListener() {
        this.collectFromState(Lifecycle.State.STARTED, viewModel.state) {
            when (it) {
                is SplashState.SignedIn -> {
                    isUserSignedIn = true
                }
                is SplashState.NotSignedIn -> {
                    isUserSignedIn = false
                }
                is SplashState.Error -> {
                    activityScope.cancel()
                    showError(it.exception)
                }
                else -> {}
            }
        }
    }

    private fun registerEventListener() {
        this.collectFromState(Lifecycle.State.STARTED, viewModel.event) {
            when (it) {
                is SplashEvent.NavigateToNextScreen -> {
                    navigateToNextScreen()
                }
            }
        }
    }

    private fun navigateToNextScreen() {
        if (isUserSignedIn) {
            activityScope.launch {
                startMainActivity()
            }
        } else {
            activityScope.launch {
                startRegistrationActivity()
            }
        }
    }

    private fun checkInternetConnection() {
        this.collectFromState(Lifecycle.State.STARTED, networkMonitor.state) {
            activityScope = CoroutineScope(Dispatchers.Main)
            when (it) {
                true -> {
                    viewModel.onAction(SplashAction.PrepareData)
                }
                false -> {
                    activityScope.cancel()
                    showError(Throwable(getString(R.string.no_internet_error_message)))
                }
                else -> {}
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.data = intentData
        startActivity(intent)
        finish()
    }

    private fun startRegistrationActivity() {
        val intent =
            Intent(this@SplashActivity, RegistrationActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.data = intentData
        startActivity(intent)
        finish()
    }

    private fun showError(error: Throwable) {
        ErrorDialogFragment.show(this.supportFragmentManager, error)
    }

    override fun onPositiveButtonClicked(error: Throwable) {
        checkInternetConnection()
    }

    override fun onNegativeButtonClicked() {
        this.finish()
    }
}