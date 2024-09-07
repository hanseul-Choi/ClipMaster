import com.android.build.api.dsl.ApplicationExtension
import com.chs.clipamaster.build_logic.convention.configureAndroidCompose
import com.chs.clipamaster.build_logic.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application") // application plugin
                apply("org.jetbrains.kotlin.android") // kotlin plugin
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this) // kotlin configure
                configureAndroidCompose(this) // compose configure

                defaultConfig.targetSdk = 34 // targetsdk
            }
        }
    }
}