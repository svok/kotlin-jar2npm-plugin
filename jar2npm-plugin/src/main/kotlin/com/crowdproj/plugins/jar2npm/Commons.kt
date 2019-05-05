package com.crowdproj.plugins.jar2npm

import org.gradle.util.GradleVersion

internal object Configurations {
    val jar2npm = "jar2npm"
    val currentTestRuntime = when {
        GradleVersion.current() >= GradleVersion.version("3.4") -> "testRuntimeOnly"
        else -> "testRuntime"
    }
}

internal object Tasks {
    val test = "test"
    val generateJar2NpmTestKitProperties = "generateJar2NpmTestKitProperties"
}
