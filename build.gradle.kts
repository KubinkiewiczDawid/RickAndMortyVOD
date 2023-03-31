plugins {
    id(DetektConfig.PLUGIN_ID).version(Versions.DETEKT_VERSION)
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.GRADLE_PLUGIN_VERSION}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN_VERSION}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAV_VERSION}")
        classpath("com.google.gms:google-services:${Versions.GOOGLE_SERVICES_VERSION}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = DetektConfig.PLUGIN_ID)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}