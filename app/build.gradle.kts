plugins {
    alias(libs.plugins.androidApplication)  // to be identical to version in build.gradle(Project)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.redderei.Blechwiki"
    compileSdk = 34
//    room { schemaDirectory(path = "$projectDir/schemas") }
    defaultConfig {
        applicationId = "org.redderei.Blechwiki"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)
//    implementation(libs.androidx.material3)
    implementation(libs.material)
    testImplementation(libs.junit.v412)
    testImplementation(libs.testng)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.picasso)
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.dash)
    implementation(libs.exoplayer.ui)
    implementation(libs.android.youtubeExtractor)
    // close compilation error
    // "Cannot fit requested classes in a single dex file (# methods: 83406 > 65536)
    implementation(libs.multidex)
    // Room components
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)
    //retrofit requisites retro+gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Rxjava
    implementation(libs.adapter.rxjava2)
    implementation(libs.rxandroid)
    //coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    //livedata
    implementation(libs.androidx.lifecycle.livedata.ktx)
}