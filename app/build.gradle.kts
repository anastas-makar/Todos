plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("com.google.devtools.ksp")
}

android {
    namespace = "pro.progr.todos"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        version = "0.0.1-alpha"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.ui:ui-tooling:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("com.google.accompanist:accompanist-insets:0.30.1")


    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.work:work-runtime-ktx:2.10.0")

    implementation("com.google.dagger:dagger:2.48")
    ksp("com.google.dagger:dagger-compiler:2.48")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("pro.progr:flow:0.1.0-alpha")
    implementation("pro.progr:diamond-api:1.0.0-alpha")
}