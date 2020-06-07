package com.crowdproj.plugins.jar2npm

import com.crowdproj.plugins.jar2npm.Configurations.jar2npm
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main class of the plugin. It registers all dependency plugins and all required tasks
 */
class KotlinJar2NpmPlugin : Plugin<Project> {

    /**
     * Main method for registering all necessary plugins and tasks
     */
    override fun apply(project: Project) {
        with(project) {
            plugins.apply(com.moowork.gradle.node.NodePlugin::class.java)
            plugins.apply(org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJsPlugin::class.java)

            val jar2NpmTask = tasks.register(
                jar2npm,
                KotlinJar2NpmTask::class.java
            )

            jar2NpmTask.get().dependsOn("yarn_install")
            tasks.getByName("processResources").dependsOn(jar2NpmTask)
        }
    }

}
