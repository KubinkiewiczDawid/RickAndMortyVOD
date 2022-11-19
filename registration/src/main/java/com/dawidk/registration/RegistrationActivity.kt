package com.dawidk.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dawidk.registration.databinding.ActivityRegistrationBinding
import com.dawidk.registration.navigation.RegistrationNavigator
import org.koin.android.ext.android.inject

class RegistrationActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityRegistrationBinding
    private val registrationNavigator: RegistrationNavigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.registration_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        registrationNavigator.navController = navController
    }
}