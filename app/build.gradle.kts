import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val debugStorePassword: String? = localProperties.getProperty("DEBUG_STORE_PASSWORD")
val debugKeyPassword: String? = localProperties.getProperty("DEBUG_KEY_PASSWORD")
val debugKeyAlias: String? = localProperties.getProperty("DEBUG_KEY_ALIAS")
val debugStoreFile = rootProject.file("debug-custom.keystore")
val useCustomDebugSigning: Boolean =
    debugStoreFile.exists() &&
            !debugStorePassword.isNullOrBlank() &&
            !debugKeyPassword.isNullOrBlank() &&
            !debugKeyAlias.isNullOrBlank()

val kakaoNativeAppKey: String =
    localProperties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

val privacyPolicyUrl: String =
    providers.gradleProperty("PRIVACY_POLICY_URL").orNull
        ?: error("gradle.properties에 PRIVACY_POLICY_URL이 없습니다.")


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jeong.runninggoaltracker"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        getByName("debug") {
            if (useCustomDebugSigning) {
                storeFile = debugStoreFile
                storePassword = debugStorePassword
                keyAlias = debugKeyAlias
                keyPassword = debugKeyPassword
            }
        }
    }

    defaultConfig {
        applicationId = "com.jeong.runninggoaltracker"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.jeong.runninggoaltracker.app.HiltTestRunner"

        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey
        buildConfigField("String", "PRIVACY_POLICY_URL", "\"$privacyPolicyUrl\"")
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
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
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":shared:designsystem"))
    implementation(project(":shared:navigation"))
    implementation(project(":feature:home"))
    implementation(project(":feature:goal"))
    implementation(project(":feature:record"))
    implementation(project(":feature:reminder"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:ai-coach"))
    implementation(project(":feature:auth"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.bundles.androidx.lifecycle.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.app)

    implementation(libs.kakao.sdk.user)

    debugImplementation(libs.bundles.androidx.compose.debug)
    debugImplementation(libs.leakcanary.android)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(libs.bundles.androidx.test)

    androidTestImplementation(libs.hilt.android.testing)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.androidx.compose.test)
}
