plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.movilidadmacas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.movilidadmacas"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.osmdroid.android)
    implementation (platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation (libs.firebase.database.ktx)
    implementation (libs.firebase.analytics)
    implementation (libs.play.services.location)
    implementation (libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)
    implementation(libs.androidx.material3.v121)
    implementation(libs.okhttp)
    implementation(libs.json)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Para dibujar polilíneas en el mapa
    implementation(libs.osmdroid.mapsforge)
    implementation(libs.androidx.room.runtime)
    kapt("androidx.room:room-compiler:2.7.2")
    implementation (libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation (libs.kotlinx.serialization.json)
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Connectivity
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.hilt.navigation.compose)

// ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.v2070) // Última versión
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.google.firebase.database.ktx)


}

apply(plugin = "com.google.gms.google-services")