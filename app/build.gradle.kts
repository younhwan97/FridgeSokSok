import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)

    alias(libs.plugins.kotlin.android.ksp)
    alias(libs.plugins.hilt.android)

    alias(libs.plugins.goolge.service)
    alias(libs.plugins.firebase.crashlytics)
}

//configurations.all {
//    resolutionStrategy.eachDependency {
//        if (requested.group == "com.intellij" && requested.name == "annotations") {
//            useTarget("org.jetbrains:annotations:23.0.0")
//        }
//    }
//}

// local.properties 사용을 위함
val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.yh.fridgesoksok"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yh.fridgesoksok"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // KAKAO API KEY
        buildConfigField("String", "KAKAO_API_KEY", properties.getProperty("KAKAO_API_KEY"))
        manifestPlaceholders["KAKAO_API_KEY"] = properties.getProperty("KAKAO_API_KEY_NO_QUOTES")

        // NAVER API KEY
        buildConfigField("String", "NAVER_CLIENT_ID", properties.getProperty("NAVER_CLIENT_ID"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", properties.getProperty("NAVER_CLIENT_SECRET"))
        buildConfigField("String", "NAVER_CLIENT_APP_NAME", properties.getProperty("NAVER_CLIENT_APP_NAME"))
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.icons)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // AndroidX
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.camerax.extensions)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.interceptor)

    // Kakao
    implementation(libs.kakao.all)

    // Naver
    implementation(libs.naver)

    // systemuicontroller
    implementation(libs.system.ui.controller)

    // Coil
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
    implementation(libs.landscapist.animation)

    // Room
    implementation(libs.room.ktx)
    ksp(libs.room.compiler.ksp)
    implementation(libs.room.runtime)

    // Coil
    implementation(libs.coil)

    // Lottie
    implementation(libs.lottie)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
}