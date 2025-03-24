plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id ("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.loginhttp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.loginhttp"
        minSdk = 21
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

    // Choose one of the following:
    // Material Design 3
    implementation (libs.material3)
    implementation (libs.androidx.material)
    implementation (libs.androidx.adaptive)

    implementation (libs.ui)

    // Android Studio Preview support
    implementation (libs.ui.tooling.preview)
    debugImplementation (libs.ui.tooling)

    // UI Tests
    androidTestImplementation (libs.ui.test.junit4)
    debugImplementation (libs.ui.test.manifest)

    // Optional - Add full set of material icons
    implementation (libs.androidx.material.icons.extended)

    // Optional - Integration with activities
    implementation (libs.androidx.activity.compose.v1100)
    // Optional - Integration with ViewModels
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation (libs.androidx.runtime.livedata)
    // Optional - Integration with RxJava
    implementation (libs.androidx.runtime.rxjava2)

    // Kotlin Serialization
    implementation (libs.kotlinx.serialization.json)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation (libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.swiperefresh)
    implementation (libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
}