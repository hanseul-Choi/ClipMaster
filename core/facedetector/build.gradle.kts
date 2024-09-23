@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.clipmaster.android.library)
    alias(libs.plugins.clipmaster.android.hilt)
}

android {
    namespace = "com.chs.clipmaster.core.facedetector"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.mlkit.face.detection)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.ui.android)
    implementation(libs.ui.android)
    implementation(libs.ui.graphics.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}