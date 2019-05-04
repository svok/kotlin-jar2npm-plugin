package com.crowdproj.plugins.jar2npm

import com.crowdproj.plugins.jar2npm.Configurations.jar2npm
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinJar2NpmPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            plugins.apply(com.moowork.gradle.node.NodePlugin::class.java)
            plugins.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin::class.java)

            val jar2NpmTask = tasks.register(
                jar2npm,
                KotlinJar2NpmTask::class.java,
                Action<KotlinJar2NpmTask> {
                    //                        latestArtifactVersion.getServerUrl().set(extension.getServerUrl())
                })

            jar2NpmTask.get().dependsOn("yarnSetup")
            tasks.getByName("processResources").dependsOn(jar2NpmTask)
//
//            configurations.maybeCreate(jar2npm)
//                .setVisible(false)
//                .description = "Copy Kotlin JAR packages to node_modules"
        }
    }

}
