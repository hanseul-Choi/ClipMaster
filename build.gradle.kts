// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false // 여기서 정의함으로써 모듈의 application plugin을 적용
    alias(libs.plugins.android.library) apply false // 여기서 정의함으로써 모듈의 library plugin을 적용
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
}