plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.jeong.fitflow.feature.mypage"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":shared:designsystem"))
    implementation(project(":shared:navigation"))

    implementation(libs.bundles.androidx.lifecycle.compose)
    implementation(libs.bundles.core.lifecycle)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.bundles.hilt.compose)

    debugImplementation(libs.bundles.androidx.compose.debug)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.androidx.compose.test)
    androidTestImplementation(libs.bundles.androidx.test)
}
