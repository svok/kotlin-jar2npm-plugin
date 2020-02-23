rootProject.name = "jar2npm"
rootProject.buildFileName = "build.gradle.kts"
//enableFeaturePreview("GRADLE_METADATA")

include("jar2npm-plugin")

pluginManagement {

    val kotlin_version: String by settings
    val gradlePluginPublishVersion: String by settings
    val jfrogBintrayVersion: String by settings
    val dokkaVersion: String by settings
    val nemerosaVersion: String by settings

    plugins {
        kotlin("jvm") version kotlin_version apply false
        id("com.gradle.plugin-publish") version gradlePluginPublishVersion apply false
        id("com.jfrog.bintray") version jfrogBintrayVersion apply false
        id("org.jetbrains.dokka") version dokkaVersion apply false
        id("net.nemerosa.versioning") version nemerosaVersion apply false
    }

//    resolutionStrategy {
//        eachPlugin {
//            when(requested.id.id) {
////                "com.crowdproj.plugins.jar2npm" -> useModule("com.crowdproj.plugins:jar2npm-plugin:${requested.version}")
//            }
//        }
//    }

    repositories {
        mavenCentral()
        maven {
            name = "Kotlin EAP (for kotlin-frontend-plugin)"
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin") }
    }
}

fun configureGradleScriptKotlinOn(project: ProjectDescriptor) {
    project.buildFileName = "build.gradle.kts"
    project.children.forEach { configureGradleScriptKotlinOn(it) }
}

configureGradleScriptKotlinOn(rootProject)
