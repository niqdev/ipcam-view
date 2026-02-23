import info.git.versionHelper.getGitCommitCount
import info.git.versionHelper.getVersionText
import info.git.versionHelper.println

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.github.niqdev.ipcam"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.niqdev.ipcam"
        minSdk = 21
        targetSdk = 36
        versionCode = "${getGitCommitCount()}0".toInt()
        versionName = "${getVersionText()}.$versionCode"
        println { "versionName=${versionName.green.bold} versionCode=${versionCode.green.bold}" }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles.addAll(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    file("proguard-rules.pro"),
                ),
            )
        }
    }
    lint {
        abortOnError = false
        disable += "MissingTranslation" + "InvalidPackage"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    implementation(project(":mjpeg-view"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22")
}
