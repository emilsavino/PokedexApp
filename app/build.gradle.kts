plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pokedex"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pokedex"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose dependencies
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Lifecycle and ViewModel for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Compose preview support (optional)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Current date
    implementation (libs.threetenabp)

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
    implementation(libs.coil.kt.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.compose.v210)

    implementation(libs.androidx.datastore.preferences.v100)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    //implementation(libs.accompanist.flowlayout)

    //Firebase authentication
    implementation(libs.firebase.analytics)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation(libs.firebase.auth)
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    val credentialsManagerVersion = "1.5.0-alpha05"
    implementation("androidx.credentials:credentials-play-services-auth:$credentialsManagerVersion")
    implementation("androidx.credentials:credentials:$credentialsManagerVersion")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("dev.gitlive:firebase-firestore:1.8.1")

}