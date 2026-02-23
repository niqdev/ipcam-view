plugins {
    id("com.android.library")
    id("maven-publish")
    id("kotlin-android")
}

android {
    namespace = "com.github.niqdev.mjpeg"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
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
    packaging {
        resources {
            excludes.add("META-INF/services/javax.annotation.processing.Processor")
        }
    }

    useLibrary("org.apache.http.legacy")

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
    implementation("androidx.appcompat:appcompat:1.6.1")
    api("io.reactivex:rxjava:1.3.8") // it"s obsolete
    api("io.reactivex:rxandroid:1.2.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.22")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                pom {
                    licenses {
                        license {
                            name = "Apache License Version 2.0"
                            url = "https://github.com/niqdev/ipcam-view/blob/master/LICENSE"
                        }
                    }
                }
            }
        }
    }
}
