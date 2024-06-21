plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.3.2").apply(false)
     kotlin("android").version("1.9.21").apply(false)
    kotlin("jvm").version("1.9.21").apply(false)
    id("com.google.devtools.ksp").version("1.9.21-1.0.16").apply(false)
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
//    ext.kotlin_version = "1.7.10"
    repositories {
        google()
        mavenCentral()
        maven ( url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
//        // NOTE: Do not place your application dependencies here; they belong
//        // in the individual module build.gradle files
    }
}