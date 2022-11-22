package com.dawidk.rickandmortyvod

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.dawidk.characters.di.charactersModule
import com.dawidk.common.di.sharedModule
import com.dawidk.core.di.coreModule
import com.dawidk.episodes.di.episodesModule
import com.dawidk.home.di.homeModule
import com.dawidk.location.di.locationModule
import com.dawidk.registration.di.registrationModule
import com.dawidk.search.di.searchModule
import com.dawidk.settings.di.settingsModule
import com.dawidk.videoplayer.di.videoModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            fragmentFactory()
            modules(
                coreModule,
                appModule,
                charactersModule,
                locationModule,
                homeModule,
                episodesModule,
                searchModule,
                sharedModule,
                registrationModule,
                settingsModule,
                videoModule
            )
        }
//        val delegate = ReachabilityWatcher { watchedObject, description ->
//            if (watchedObject::class.java.name != "com.google.firebase.auth.api.fallback.service.FirebaseAuthFallbackService instance") {
//                AppWatcher.objectWatcher.expectWeaklyReachable(watchedObject, description)
//            }
//        }
//        val watchersToInstall = AppWatcher.appDefaultWatchers(this, delegate)
//
//        AppWatcher.manualInstall(
//            application = this,
//            watchersToInstall = watchersToInstall
//        )
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))
                    .build()
            }
            .build()
    }
}