package com.dawidk.registration.di

import com.dawidk.common.navigation.RegistrationActivityNavigator
import com.dawidk.common.registration.FirebaseAuthApi
import com.dawidk.common.registration.GoogleClientApi
import com.dawidk.registration.FirebaseAuthClient
import com.dawidk.registration.GoogleClientApiWrapper
import com.dawidk.registration.SignInViewModel
import com.dawidk.registration.SignUpViewModel
import com.dawidk.registration.navigation.RegistrationActivityNavigatorHandler
import com.dawidk.registration.navigation.RegistrationNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registrationModule = module {
    viewModel { SignInViewModel(get(), get(), get(), get()) }
    viewModel { SignUpViewModel(get(), get(), get()) }
    single<GoogleClientApi> { GoogleClientApiWrapper(get()) }
    single { RegistrationNavigator(get(), get()) }
    single<FirebaseAuthApi> { FirebaseAuthClient() }
    single<RegistrationActivityNavigator> { RegistrationActivityNavigatorHandler(get()) }
}