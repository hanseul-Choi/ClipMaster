@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.clipmaster.android.library.compose)
    alias(libs.plugins.clipmaster.android.hilt)
}

android {
    namespace = "com.chs.clipmaster.feature.camera"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:facedetector"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.camerax.camera2)
    implementation(libs.androidx.camerax.core)
    implementation(libs.androidx.camerax.lifecycle)
    implementation(libs.androidx.camerax.view)

    implementation(libs.androidx.compose.material3)
    implementation(libs.coil.kt.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}