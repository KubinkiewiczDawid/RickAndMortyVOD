//import com.google.protobuf.gradle.*
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.apollographql.apollo").version(Versions.APOLLO_VERSION)
    id("androidx.navigation.safeargs.kotlin")
}

apollo {
    generateKotlinModels.set(true)
}

android {
    compileSdk = AppConfig.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AppConfig.MIN_SDK_VERSION
        targetSdk = AppConfig.TARGET_SDK_VERSION

        testInstrumentationRunner = AppConfig.ANDROID_TEST_INSTRUMENTATION
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core"))
    implementation(project(":videoplayer"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(AppDependencies.homeLibraries)
    testImplementation(AppDependencies.testLibraries)
    androidTestImplementation(AppDependencies.androidTestLibraries)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
