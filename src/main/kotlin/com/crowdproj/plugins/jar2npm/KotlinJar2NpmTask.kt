package com.crowdproj.plugins.jar2npm

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.nio.file.Files

open class KotlinJar2NpmTask: DefaultTask() {
    @Input
    val conf: Configuration = project.configurations.getByName("testRuntimeClasspath")

    @OutputFile
    val nmReal = project.projectDir.resolve("node_modules").mkDirOrFail()
    @OutputFile
    val nmImported = project.buildDir.resolve("node_modules_imported").mkDirOrFail()

    init {
        group = "node"
        description = "Attaches Kotlin JAR files to node_modules"
    }

    @TaskAction
    fun createJacocoProperties() {
        val allJars = conf
            .resolvedConfiguration
            .resolvedArtifacts

        val names = allJars
            .filter { it.file.isFile && it.file.exists() }
            .distinctBy { it.file.canonicalFile.absolutePath }
            .map {
                val version = it.moduleVersion.id.version
                val file = it.file

                val metaName = project
                    .zipTree(file)
                    .find { fName -> fName.name.endsWith(".meta.js") }
                    ?.name
                    ?: return@map ""

                val name = metaName.replace("\\.meta\\.js\$".toRegex(), "")
                val js = "$name.js"

                val outDir = nmImported.resolve(name).mkDirOrFail()
                project.copy {
                    it.from(project.zipTree(file))
                    it.into(outDir)
                }
                val packageJson = mapOf(
                    "name" to name,
                    "version" to version,
                    "main" to js,
                    "_source" to "gradle"
                )

                outDir.resolve("package.json").bufferedWriter().use { out ->
                    out.appendln(JsonBuilder(packageJson).toPrettyString())
                }
                name
            }
            .filter { it.isNotBlank() }


        doLast {
            names.forEach { name ->
                val outDir = nmImported.resolve(name).mkDirOrFail()
                nmReal.resolve(name).ensureSymlink(outDir)
            }
        }
    }
}

private fun File.mkDirOrFail(): File {
    if (!mkdirs() && !exists()) {
        throw IOException("Failed to create directories at $this")
    }
    return this
}

private fun File.ensureSymlink(file: File) {
    if (this.exists() && Files.isSymbolicLink(toPath())) return
    Files.createSymbolicLink(toPath(), file.toPath())
}
