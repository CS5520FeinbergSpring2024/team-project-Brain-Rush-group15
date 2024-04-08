val lottieVersion: String = "3.4.0"
val picassoVersion: String = "2.8"
val firebaseVersion: String = "23.4.1"
val volleyVersion: String = "1.2.1"
val okhttpVersion: String = "4.9.0"

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.northeastern.brainrush"
    compileSdk = 34
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "edu.northeastern.brainrush"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "ChatGPT_API_KEY", "${property("chatGPT_API_KEY")}")

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
}

dependencies {
    api("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.picasso:picasso:$picassoVersion")
    implementation("com.airbnb.android:lottie:$lottieVersion")
    implementation("com.google.firebase:firebase-messaging:$firebaseVersion")
    implementation("com.android.volley:volley:$volleyVersion")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.android.material:material:1.11.0")
}