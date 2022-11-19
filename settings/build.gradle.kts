plugins {
    id("com.android.library")
    id("kotlin-android")
    id("androidx.navigation.safeargs")
}

android {
    compileSdk = (AppConfig.COMPILE_SDK_VERSION)

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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":common"))
    implementation(AppDependencies.settingsLibraries)
    testImplementation(AppDependencies.testLibraries)
    androidTestImplementation(AppDependencies.androidTestLibraries)
    implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM_VERSION}"))
}