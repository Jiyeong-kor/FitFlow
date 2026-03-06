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
val hasDebugCredentials: Boolean =
    listOf(debugStorePassword, debugKeyPassword, debugKeyAlias)
        .all { !it.isNullOrBlank() }
val useCustomDebugSigning: Boolean = debugStoreFile.exists() && hasDebugCredentials

val releaseStorePassword: String? = localProperties.getProperty("RELEASE_STORE_PASSWORD")
val releaseKeyPassword: String? = localProperties.getProperty("RELEASE_KEY_PASSWORD")
val releaseKeyAlias: String? = localProperties.getProperty("RELEASE_KEY_ALIAS")
val releaseStoreFilePath: String? = localProperties.getProperty("RELEASE_STORE_FILE")
val releaseStoreFile = releaseStoreFilePath?.let { rootProject.file(it) }

val hasReleaseCredentials: Boolean =
    listOf(releaseStorePassword, releaseKeyPassword, releaseKeyAlias, releaseStoreFilePath)
        .all { !it.isNullOrBlank() }
val useReleaseSigning: Boolean = hasReleaseCredentials && (releaseStoreFile?.exists() == true)

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
    namespace = "com.jeong.fitflow"
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

        create("release") {
            if (useReleaseSigning) {
                storeFile = releaseStoreFile
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    defaultConfig {
        applicationId = "com.jeong.fitflow"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.jeong.fitflow.app.HiltTestRunner"

        buildConfigField("String", "PRIVACY_POLICY_URL", "\"$privacyPolicyUrl\"")
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isShrinkResources = true
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
    implementation(project(":feature:aicoach"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:goal"))
    implementation(project(":feature:home"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:record"))
    implementation(project(":feature:reminder"))
    implementation(project(":shared:designsystem"))
    implementation(project(":shared:navigation"))

    implementation(libs.bundles.lifecycle.full)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.compose)

    implementation(libs.bundles.hilt.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase.app)

    debugImplementation(libs.bundles.androidx.compose.debug)
    debugImplementation(libs.leakcanary.android)

    ksp(libs.hilt.compiler)

    testImplementation(libs.bundles.test.unit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.androidx.compose.test)
    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.hilt.android.testing)
}
