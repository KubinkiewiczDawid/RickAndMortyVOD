package com.dawidk.core

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloClientAwarenessInterceptor
import com.apollographql.apollo.cache.http.ApolloHttpCache
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

const val BASE_URL = "https://rickandmortyapi.com/graphql"

class ApolloContext(
    clientName: String,
    clientVersion: String,
    private val showLogs: Boolean,
    context: Context
) {

    private val okHttp = OkHttpClient
        .Builder()
        .addInterceptor(ApolloClientAwarenessInterceptor(clientName, clientVersion)).apply {
            if (showLogs) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }
        }
    val file = File(context.cacheDir, "apolloCache")
    val size: Long = 1024 * 1024
    private val cacheStore = DiskLruHttpCacheStore(file, size)
    internal val apolloClient: ApolloClient = ApolloClient.builder()
        .serverUrl(BASE_URL)
        .httpCache(ApolloHttpCache(cacheStore))
        .okHttpClient(okHttp.build())
        .build()
}