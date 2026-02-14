import org.gradle.kotlin.dsl.project

pluginManagement {
    repositories {
        google()
        // google {
        //    content {
        //        includeGroupByRegex("com\\.android.*")
        //        includeGroupByRegex("com\\.google.*")
        //        includeGroupByRegex("androidx.*")
        //    }
        //}
        mavenCentral()
        mavenLocal()
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

rootProject.name = "Bluetooth_chat"
include(":app")
 