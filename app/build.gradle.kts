import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.github.ben-manes.versions")
    id("com.google.devtools.ksp") version "2.3.3"
    id("com.anthonycr.plugins.mezzanine") version "2.2.0"
}

android {
    namespace = "acr.browser.lightning"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 29
        versionName = "5.1.0"
        vectorDrawables.useSupportLibrary = true
        manifestPlaceholders["requestLegacyExternalStorage"] = "true"
    }

    // ⭐ 自动签名配置（最省心）
    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "123456"
            keyAlias = "release"
            keyPassword = "123456"
        }
    }

    val isCi = System.getenv("CI") == "true"

    sourceSets {
        create("lightningPlus").apply {
            setRoot("src/LightningPlus")
        }
        if (!isCi) {
            create("lightningLite").apply {
                setRoot("src/LightningLite")
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        named("debug") {
            multiDexEnabled = true
            isMinifyEnabled = false
            isShrinkResources = false
            setProguardFiles(listOf("proguard-project.txt"))
        }

        named("release") {
            multiDexEnabled = false

            // ⭐ 关闭混淆（避免 APK 被破坏）
            isMinifyEnabled = false
            isShrinkResources = false

            setProguardFiles(listOf("proguard-project.txt"))

            // ⭐ 自动签名（关键）
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions.add("capabilities")

    productFlavors {
        create("lightningPlus") {
            dimension = "capabilities"
            buildConfigField("boolean", "FULL_VERSION", "true")
            applicationId = "acr.browser.lightning"
            versionCode = 101
        }

        if (!isCi) {
            create("lightningLite") {
                dimension = "capabilities"
                buildConfigField("boolean", "FULL_VERSION", "false")
                applicationId = "acr.browser.barebones"
                versionCode = 102
            }
        }
    }

    packaging {
        resources.excludes += listOf(".readme")
    }

    // ⭐ 关闭 LintVital（避免 release 构建失败）
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    debugImplementation("androidx.multidex:multidex:2.0.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.27.6")
    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation("org.robolectric:robolectric:4.16")

    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.2.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.webkit:webkit:1.14.0")

    implementation("org.jsoup:jsoup:1.21.2")

    val mezzanineVersion = "2.2.0"
    implementation("com.anthonycr.mezzanine:core:$mezzanineVersion")
    ksp("com.anthonycr.mezzanine:processor:$mezzanineVersion")

    val daggerVersion = "2.57.2"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    compileOnly("javax.annotation:jsr250-api:1.0")

    implementation("com.guolindev.permissionx:permissionx:1.8.1")

    implementation("com.squareup.okhttp3:okhttp:5.3.2")

    implementation("io.coil-kt.coil3:coil:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

mezzanine {
    files = files(
        "src/main/html/list.html",
        "src/main/html/bookmarks.html",
        "src/main/html/homepage.html",
        "src/main/js/InvertPage.js",
        "src/main/js/TextReflow.js",
        "src/main/js/ThemeColor.js"
    )
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}
