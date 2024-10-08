pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ClipMaster"
include(":app")
include("core:data")
include(":core:model")
include(":core:navigation")
include(":feature:camera")
include(":core:designsystem")
include(":core:gallery")
include(":core:facedetector")
