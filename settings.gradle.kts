pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // ✅ 여기에 추가
        maven { setUrl("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ✅ 여기도 추가
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "enviroment"
include(":environmentmoniter")
