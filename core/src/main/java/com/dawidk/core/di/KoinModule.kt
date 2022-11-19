package com.dawidk.core.di

import com.dawidk.core.ApolloContext
import com.dawidk.core.RickAndMortyAPI
import com.dawidk.core.datastore.EpisodesInfoDataStoreRepository
import com.dawidk.core.datastore.HomeScreenDataStoreRepository
import com.dawidk.core.datastore.UserCredentialsDataStoreRepository
import com.dawidk.core.datastore.VideoStateDataStoreRepository
import com.dawidk.core.executors.FetchCharacterByIdExecutor
import com.dawidk.core.executors.FetchCharactersByIdsExecutor
import com.dawidk.core.executors.FetchCharactersListExecutor
import com.dawidk.core.executors.FetchCharactersListInfoExecutor
import com.dawidk.core.executors.FetchEpisodeByIdExecutor
import com.dawidk.core.executors.FetchEpisodesListExecutor
import com.dawidk.core.executors.FetchEpisodesListInfoExecutor
import com.dawidk.core.executors.FetchLocationByIdExecutor
import com.dawidk.core.executors.FetchLocationsListExecutor
import com.dawidk.core.executors.FetchLocationsListInfoExecutor
import com.dawidk.core.executors.UpdateCacheExecutor
import com.dawidk.core.firestore.FirestoreClient
import com.dawidk.core.firestore.FirestoreClientApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { ApolloContext("", "", false, androidContext()) }
    factory { RickAndMortyAPI(get()) }
    single { FetchCharacterByIdExecutor(get()) }
    single { FetchCharactersListExecutor(get()) }
    single { FetchCharactersByIdsExecutor(get()) }
    single { FetchLocationsListExecutor(get()) }
    single { FetchLocationByIdExecutor(get()) }
    single { FetchCharactersListInfoExecutor(get()) }
    single { FetchLocationsListInfoExecutor(get()) }
    single { FetchEpisodeByIdExecutor(get()) }
    single { FetchEpisodesListInfoExecutor(get()) }
    single { FetchEpisodesListExecutor(get()) }
    single { HomeScreenDataStoreRepository(get()) }
    single { UserCredentialsDataStoreRepository(get()) }
    single { VideoStateDataStoreRepository(get()) }
    single { EpisodesInfoDataStoreRepository(get()) }
    single { UpdateCacheExecutor(get()) }
    single<FirestoreClientApi> { FirestoreClient(get(), CoroutineScope(Dispatchers.Main)) }
}