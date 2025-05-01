plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
}

val uploadKeystorePath: String by project
val uploadStorePassword: String by project
val uploadKeystoreAlias: String by project
val uploadKeyPassword: String by project

android {
    namespace = "com.happyplaces"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.linli.happyplace"
        minSdk = 23
        targetSdk = 35
        versionCode = 6
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(uploadKeystorePath)
            storePassword = uploadStorePassword
            keyAlias = uploadKeystoreAlias
            keyPassword = uploadKeyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            resValue("string", "app_name", "Happy Places($name)")
            isDebuggable = true
            applicationIdSuffix = ".debug"
            // Disables PNG crunching for the "debug" build type.
            isCrunchPngs = false
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ─── Android ──────────────────────────────────────────────────────
    implementation(libs.material)
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
    implementation (libs.androidx.navigation.compose)
    implementation (libs.androidx.runtime)
    implementation (libs.ui)

    // ─── Room ──────────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    // ─── Retrofit + OkHttp BOM ───────────────────────────────────────
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ─── Koin DI ──────────────────────────────────────────────────────
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // ─── Google Maps / Places ────────────────────────────────────────
    implementation(libs.play.services.maps)
    implementation(libs.places)
    // Optionally, you can include the Compose utils library for Clustering,
    // Street View metadata checks, etc.
    implementation (libs.maps.compose.utils)

    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation (libs.maps.compose.widgets)
    implementation(libs.maps.compose)

    // ─── 其他 UI／權限工具 ───────────────────────────────────────────
    implementation(libs.circleimageview)
    implementation(libs.dexter)
    implementation(libs.coil.compose)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation(libs.kotlinx.serialization.json)

    // ─── Firebase ──────────────────────────────────────────────────────
    implementation(libs.firebase.firestore)
}
