package com.dawidk.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dawidk.settings.databinding.ActivitySettingsBinding
import com.dawidk.settings.navigation.SettingsNavigator
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivitySettingsBinding
    private val settingsNavigator: SettingsNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.settings_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        settingsNavigator.navController = navController
    }
}