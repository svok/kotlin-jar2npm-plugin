rootProject.name = "jar2npm"
rootProject.buildFileName = "build.gradle.kts"
//enableFeaturePreview("GRADLE_METADATA")

//pluginManagement {
//    repositories {
//        gradlePluginPortal()
//    }
//    resolutionStrategy {
//        eachPlugin {
//            when (requested.id.id) {
//                "gradle-bintray-plugin" -> useModule("com.jfrog.bintray.gradle:gradle-bintray-plugin:${requested.version}")
//            }
//        }
//    }
//}

fun configureGradleScriptKotlinOn(project: ProjectDescriptor) {
    project.buildFileName = "build.gradle.kts"
    project.children.forEach { configureGradleScriptKotlinOn(it) }
}

configureGradleScriptKotlinOn(rootProject)
