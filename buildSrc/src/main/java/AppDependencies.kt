import org.gradle.api.artifacts.dsl.DependencyHandler

object AppDependencies {

    private const val KOTLIN_STD_LIB =
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.KOTLIN_VERSION}"
    private const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT_VERSION}"
    private const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX_VERSION}"
    private const val EXT_JUNIT = "androidx.test.ext:junit:${Versions.EXT_JUNIT_VERSION}"
    private const val ESPRESSO_CORE =
        "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_VERSION}"
    private const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL_VERSION}"
    private const val APOLLO_RUNTIME =
        "com.apollographql.apollo:apollo-runtime:${Versions.APOLLO_VERSION}"
    private const val APOLLO_COROUTINES_SUPPORT =
        "com.apollographql.apollo:apollo-coroutines-support:${Versions.APOLLO_VERSION}"
    private const val KOIN = "io.insert-koin:koin-android:${Versions.KOIN_VERSION}"
    private const val KOIN_EXT = "io.insert-koin:koin-android-ext:${Versions.KOIN_VERSION}"
    private const val COROUTINES =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES_VERSION}"
    private const val LIFECYCLE_VIEWMODEL_KTX =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFCECYCLE_VIEWMODEL_VERSION}"
    private const val LIVEDATA_KTX =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFCECYCLE_VIEWMODEL_VERSION}"
    private const val LOGGING_INTERCEPTOR =
        "com.squareup.okhttp3:logging-interceptor:${Versions.LOGGING_INTERCEPTOR_VERSION}"
    private const val MOCKK = "io.mockk:mockk:${Versions.MOCKK_VERSION}"
    private const val JUNIT_JUPITER_API =
        "org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT_VERSION}"
    private const val JUPITER_ENGINE =
        "org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT_VERSION}"
    private const val COIL = "io.coil-kt:coil:${Versions.COIL_VERSION}"
    private const val LIFECYCLE_RUNTIME_KTX =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_RUNTIME_KTX_VERSION}"
    private const val NAV_FRAGMENT_KTX =
        "androidx.navigation:navigation-fragment-ktx:${Versions.NAV_VERSION}"
    private const val NAV_UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAV_VERSION}"
    private const val NAV_MODULE_SUPP =
        "androidx.navigation:navigation-dynamic-features-fragment:${Versions.NAV_VERSION}"
    private const val NAV_TESTING = "androidx.navigation:navigation-testing:${Versions.NAV_VERSION}"
    private const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_VERSION}"
    private const val FRAGMENT_TESTING_KTX =
        "androidx.fragment:fragment-testing:${Versions.FRAGMENT_VERSION}"
    private const val PAGING3 = "androidx.paging:paging-runtime-ktx:${Versions.PAGING_VERSION}"
    private const val DATASTORE = "androidx.datastore:datastore:${Versions.DATASTORE_VERSION}"
    private const val DATASTORE_CORE =
        "androidx.datastore:datastore-core:${Versions.DATASTORE_VERSION}"
    private const val DATASTORE_PROTOBUF =
        "com.google.protobuf:protobuf-javalite:${Versions.DATASTORE_PROTOBUF_VERSION}"
    private const val PLAY_SERVICES_AUTH =
        "com.google.android.gms:play-services-auth:${Versions.PLAY_SERVICES_AUTH_VERSION}"
    private const val CONSTRAINT_LAYOUT =
        "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT_VERSION}"
    private const val APOLLO_HTTP_CACHE =
        "com.apollographql.apollo:apollo-http-cache:${Versions.APOLLO_VERSION}"
    private const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
    private const val FIREBASE_AUTH_KTX = "com.google.firebase:firebase-auth-ktx"
    private const val LIFECYCLE_RUNTIME =
        "androidx.lifecycle:lifecycle-runtime:${Versions.LIFECYCLE_VERSION}"
    private const val LIFECYCLE_EXTENSIONS =
        "androidx.lifecycle:lifecycle-extensions:${Versions.LIFECYCLE_EXTENSIONS_VERSION}"
    private const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
    private const val FIREBASE_DYNAMIC_LINKS = "com.google.firebase:firebase-dynamic-links-ktx"
    private const val EXO_PLAYER =
        "com.google.android.exoplayer:exoplayer:${Versions.EXO_PLAYER_VERSION}"
    private const val GSON = "com.google.code.gson:gson:${Versions.GSON_VERSION}"
    private const val MEDIA_ROUTER =
        "androidx.mediarouter:mediarouter:${Versions.MEDIA_ROUTER_VERSION}"
    private const val PLAY_SERVICES_CAST =
        "com.google.android.gms:play-services-cast-framework:${Versions.PLAY_SERVICES_CAST_VERSION}"
    private const val FIRESTORE = "com.google.firebase:firebase-firestore-ktx"
    private const val LEAKCANARY =
        "com.squareup.leakcanary:leakcanary-android:${Versions.LEAKCANARY_VERSION}"
    private const val CAST =
        "com.google.android.gms:play-services-cast-framework:${Versions.CAST_VERSION}"
    private const val EXOPLAYER_IMA_EXTENSION =
        "com.google.android.exoplayer:extension-ima:${Versions.EXO_PLAYER_VERSION}"
    private const val MOSHI = "com.squareup.moshi:moshi:${Versions.MOSHI_VERSION}"
    private const val MOSHI_KOTLIN = "com.squareup.moshi:moshi-kotlin:${Versions.MOSHI_VERSION}"

    private val firebaseDependencies = arrayListOf<String>().apply {
        add(FIREBASE_ANALYTICS)
        add(FIREBASE_AUTH_KTX)
        add(FIREBASE_MESSAGING)
        add(FIREBASE_DYNAMIC_LINKS)
        add(FIRESTORE)
    }
    private val navigationDependencies = arrayListOf<String>().apply {
        add(NAV_FRAGMENT_KTX)
        add(NAV_UI_KTX)
        add(NAV_MODULE_SUPP)
    }
    private val datastoreDependencies = arrayListOf<String>().apply {
        add(DATASTORE)
        add(DATASTORE_CORE)
        add(DATASTORE_PROTOBUF)
    }

    val appLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(FRAGMENT_KTX)
        add(CONSTRAINT_LAYOUT)
        addAll(navigationDependencies)
        add(COIL)
        add(PLAY_SERVICES_AUTH)
        addAll(datastoreDependencies)
        addAll(firebaseDependencies)
        add(EXO_PLAYER)
        add(MEDIA_ROUTER)
        add(PLAY_SERVICES_CAST)
        add(LEAKCANARY)
        add(CAST)
        add(EXOPLAYER_IMA_EXTENSION)
    }
    val characterLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(COIL)
        add(LIFECYCLE_RUNTIME_KTX)
        add(LIVEDATA_KTX)
        addAll(navigationDependencies)
        add(PAGING3)
    }
    val coreLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(APOLLO_RUNTIME)
        add(APOLLO_COROUTINES_SUPPORT)
        add(KOIN)
        add(KOIN_EXT)
        add(LOGGING_INTERCEPTOR)
        add(PAGING3)
        add(APOLLO_HTTP_CACHE)
        addAll(datastoreDependencies)
        add(PLAY_SERVICES_AUTH)
        addAll(firebaseDependencies)
    }
    val locationLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(LIFECYCLE_RUNTIME_KTX)
        addAll(navigationDependencies)
        add(PAGING3)
    }
    val homeLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(COIL)
        add(LIFECYCLE_RUNTIME_KTX)
        add(LIVEDATA_KTX)
        addAll(navigationDependencies)
        addAll(datastoreDependencies)
    }
    val episodesLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(COIL)
        add(LIFECYCLE_RUNTIME_KTX)
        add(LIVEDATA_KTX)
        addAll(navigationDependencies)
        add(PAGING3)
        add(EXO_PLAYER)
        add(GSON)
        add(LIFECYCLE_EXTENSIONS)
        add(LIFECYCLE_RUNTIME)
        addAll(datastoreDependencies)
        addAll(firebaseDependencies)
        add(EXOPLAYER_IMA_EXTENSION)
    }
    val commonLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(LIFECYCLE_RUNTIME_KTX)
        add(COIL)
        add(KOIN)
        add(KOIN_EXT)
        add(EXO_PLAYER)
        addAll(navigationDependencies)
        addAll(datastoreDependencies)
        addAll(firebaseDependencies)
        add(MEDIA_ROUTER)
        add(EXOPLAYER_IMA_EXTENSION)
    }
    val searchLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(LIFECYCLE_RUNTIME_KTX)
        addAll(navigationDependencies)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(COIL)
    }
    val registrationLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(LIFECYCLE_RUNTIME_KTX)
        addAll(navigationDependencies)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        add(PLAY_SERVICES_AUTH)
        addAll(datastoreDependencies)
        addAll(firebaseDependencies)
    }
    val settingsLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(LIFECYCLE_RUNTIME_KTX)
        addAll(navigationDependencies)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        add(LIFECYCLE_VIEWMODEL_KTX)
        addAll(datastoreDependencies)
        addAll(firebaseDependencies)
    }
    val videoPlayerLibraries = arrayListOf<String>().apply {
        add(KOTLIN_STD_LIB)
        add(CORE_KTX)
        add(APPCOMPAT)
        add(MATERIAL)
        add(LIFECYCLE_RUNTIME_KTX)
        add(LIFECYCLE_VIEWMODEL_KTX)
        addAll(navigationDependencies)
        add(EXO_PLAYER)
        add(GSON)
        add(LIFECYCLE_EXTENSIONS)
        add(LIFECYCLE_RUNTIME)
        add(KOIN)
        add(KOIN_EXT)
        add(COROUTINES)
        addAll(datastoreDependencies)
        add(MEDIA_ROUTER)
        add(PLAY_SERVICES_CAST)
        add(EXOPLAYER_IMA_EXTENSION)
        add(MOSHI)
        add(MOSHI_KOTLIN)
    }
    val androidTestLibraries = arrayListOf<String>().apply {
        add(EXT_JUNIT)
        add(ESPRESSO_CORE)
        add(MOCKK)
        add(JUNIT_JUPITER_API)
        add(NAV_TESTING)
        add(FRAGMENT_TESTING_KTX)
    }
    val testLibraries = arrayListOf<String>().apply {
        add(JUPITER_ENGINE)
        add(MOCKK)
    }
}

fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}