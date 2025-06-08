plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id ("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.prueba2tfg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prueba2tfg"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    android {
        // Otras configuraciones

        packagingOptions {
            resources.excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
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
        isCoreLibraryDesugaringEnabled= true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.ads.mobile.sdk)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation ("io.coil-kt:coil-compose:2.2.2")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.1.3")
    implementation("androidx.compose.ui:ui-tooling:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.1")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.api-client:google-api-client-android:2.2.0")
    implementation ("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation ("com.google.http-client:google-http-client-gson:1.43.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // O la versión más reciente
    implementation ("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")
    implementation ("com.google.api-client:google-api-client-android:1.32.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")


}