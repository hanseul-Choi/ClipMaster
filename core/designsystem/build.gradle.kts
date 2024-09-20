@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.clipmaster.android.library.compose)
    alias(libs.plugins.clipmaster.android.hilt)
}

android {
    namespace = "com.chs.clipmaster.core.designsystem"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}