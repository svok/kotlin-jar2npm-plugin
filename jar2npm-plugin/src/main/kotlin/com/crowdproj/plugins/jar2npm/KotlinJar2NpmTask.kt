package com.crowdproj.plugins.jar2npm

import groovy.json.JsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * Task that extracts extracs data from jar packages into npm ones
 */
open class KotlinJar2NpmTask : DefaultTask() {

    private val log = LoggerFactory.getLogger(this::class.java)!!

    /**
     * The configuration used to extract all dependencies
     */
    @Input
    val conf: Configuration = project
        .configurations
        .getByName("testRuntimeClasspath")
        .also {
            log.debug("Setting default configuration: {}", it.name)
        }

    /**
     * A reference to the `node_modules` directory
     */
    @OutputDirectory
    val nodeModules = project
        .projectDir
        .resolve("node_modules")
        .mkDirOrFail()
        .also {
            log.debug("Setting default node_modules: {}", it.absoluteFile)
        }

    /**
     * A reference to the folder with extracted jar packages
     */
    @OutputDirectory
    val nodeModulesImported = project
        .buildDir
        .resolve("node_modules_imported")
        .mkDirOrFail()
        .also {
            log.debug("Setting default node_modules_imported: {}", it.absoluteFile)
        }

    init {
        group = "node"
        description = "Attach Kotlin JAR files to node_modules"
    }


    /**
     * The task that extracts data from Jar packages into `node_modules` npm package
     */
    @TaskAction
    fun jar2npm() {
        log.info("Starting jar2npm")
        val allJars = conf
            .resolvedConfiguration
            .resolvedArtifacts
        log.debug("All JARs: {}", allJars.map { it.name })

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

                val outDir = nodeModulesImported.resolve(name).mkDirOrFail()
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


        log.info("Create symlinks: {}", names)
        names.forEach { name ->
            val outDir = nodeModulesImported.resolve(name).mkDirOrFail()
            log.debug("Create symlink for {} to {}", name, outDir)
            nodeModules.resolve(name).ensureSymlink(outDir)
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
