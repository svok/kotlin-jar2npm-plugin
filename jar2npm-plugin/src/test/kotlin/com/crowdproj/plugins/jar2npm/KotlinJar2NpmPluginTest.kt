package com.crowdproj.plugins.jar2npm

import com.crowdproj.plugins.jar2npm.Configurations.jar2npm
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.Plugin
import org.gradle.api.Project
//import org.gradle.kotlin.dsl.get
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

internal class KotlinJar2NpmPluginTest {
    val project: Project by lazy {
        ProjectBuilder.builder().build().also {
            it.pluginManager.apply(KotlinJar2NpmPlugin::class.java)
            it.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-js:1.3.61")
        }
    }

    @Test
    fun pluginLoaded() {
        val task = project.tasks.getByName(jar2npm)
        assertThat(task).isInstanceOf(KotlinJar2NpmTask::class.java)
    }

    @Test
    fun nodePluginLoaded() {
        val pluginName = "com.github.node-gradle.node"
        val plugin = project.plugins.findPlugin(pluginName)
        assertThat(plugin).isInstanceOf(Plugin::class.java)
    }

    @Test
    fun kotlinPluginLoaded() {
        val pluginName = "kotlin-platform-js"
        val plugin = project.plugins.findPlugin(pluginName)
        assertThat(plugin).isInstanceOf(Plugin::class.java)
    }

}
