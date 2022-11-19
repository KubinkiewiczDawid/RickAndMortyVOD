import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.apollographql.apollo").version(Versions.APOLLO_VERSION)
    id("com.google.protobuf").version(Versions.DATASTORE_PROTOBUF_PLUGIN_VERSION)
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(AppDependencies.coreLibraries)
    testImplementation(AppDependencies.testLibraries)
    testImplementation(files("ae_android_mobile/core/src/test/resources"))
    androidTestImplementation(AppDependencies.androidTestLibraries)
    implementation(platform("com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM_VERSION}"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.DATASTORE_PROTOBUF_VERSION}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
