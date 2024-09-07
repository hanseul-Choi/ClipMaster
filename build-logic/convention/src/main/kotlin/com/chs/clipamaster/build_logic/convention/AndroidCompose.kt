package com.chs.clipamaster.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
)  {
    // libs.toml의 정보를 참고
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            // findVersion -> libs.toml의 Versions 정보를 참고
            kotlinCompilerExtensionVersion = libs.findVersion("androidxComposeCompiler").get().toString()
        }

        dependencies {
            // findLibrary -> libs.toml의 libraries 정보를 참고
            val bom = libs.findLibrary("androidx-compose-bom").get()

            // bom 정보만 적용
            add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
        }
    }
}
