import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Date
import java.time.format.DateTimeFormatter
import java.time.OffsetDateTime

plugins {
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
    id("net.nemerosa.versioning")
}

val junitVersion: String by project

//defaultTasks.add("jar")

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.moowork.gradle:gradle-node-plugin:1.3.1")
    implementation(kotlin("gradle-plugin"))

    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.12.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

pluginBundle {
    website = "https://github.com/svok/kotlin-jar2npm-plugin"
    vcsUrl = "https://github.com/svok/kotlin-jar2npm-plugin.git"
    tags = listOf("jar", "npm", "node", "nodejs", "kotlin", "javascript", "react", "angular")
}

gradlePlugin {
    plugins {
        create("kotlin-jar2npm-plugin") {
            id = "com.crowdproj.plugins.jar2npm"
            displayName = "Kotlin Jar2Npm Plugin"
            description = "Plugin to extract Kotlin JAR JS packages to node_modules folder"
            implementationClass = "com.crowdproj.plugins.jar2npm.KotlinJar2NpmPlugin"
        }
    }
}

tasks {

    dokka {
        val dokkaOut = "$buildDir/docs"
        outputFormat = "html"
        outputDirectory = dokkaOut

        // This will force platform tags for all non-common sources e.g. "JVM"
        impliedPlatforms = mutableListOf("Common", "JVM")

        // dokka fails to retrieve sources from MPP-tasks so they must be set empty to avoid exception
        kotlinTasks(closureOf<Any?> { emptyList<Any?>() })
    }

    val jar by getting(Jar::class) {
        manifest {
            val buildTimeAndDate = OffsetDateTime.now()
            val buildDate by extra { DateTimeFormatter.ISO_LOCAL_DATE.format(buildTimeAndDate) }
            val buildTime by extra { DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ").format(buildTimeAndDate) }
            val buildRevision by extra { versioning.info.commit }

            attributes(
                mutableMapOf(
                    "Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty(
                        "java.vm.version"
                    )})",
                    "Built-By" to "Sergey Okatov",
                    "Build-Date" to buildDate,
                    "Build-Time" to buildTime,
                    "Build-Revision" to buildRevision,
                    "Specification-Title" to project.name,
                    "Specification-Version" to project.version as String,
                    "Specification-Vendor" to "Sergey Okatov",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Implementation-Vendor" to "Sergey Okatov"
                )
            )
        }
    }

    val dokkaJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        dependsOn(dokka) // not needed; dependency automatically inferred by from(tasks.dokka)
        from(dokka.get().outputDirectory)
    }

    // Create sources Jar from main kotlin sources
    val sourcesJar by creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles sources JAR"
        archiveClassifier.set("sources")
        from(
            listOf(
                "src/commonMain/kotlin",
                "src/jvmMain/kotlin"
            ).map { projectDir.resolve(it) }
        )
    }

    publishing {
        publications {
            register("mavenJava", MavenPublication::class) {
                pom {

                    name.set("kotlin-jar2npm-plugin")
                    description.set("Gradle plugin to extract Kotlin-JS JAR packages to Node node_modules folder to use them in JavaScript projects like React, Angular, etc.")
                    url.set("https://github.com/svok/kotlin-jar2npm-plugin")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("svok")
                            name.set("Sergey Okatov")
                            email.set("sokatov@gmail.com")
                        }
                    }

                }
                from(getComponents().get("kotlin"))
                artifact(sourcesJar)
                artifact(dokkaJar)
            }
        }
        repositories {
            maven {
                url = uri("$buildDir/repository")
            }
        }
    }

    bintray {
        user = System.getenv("bintrayUser")?.toString() ?: ""
        key = System.getenv("bintrayApiKey")?.toString() ?: ""
        override = true
        setPublications("mavenJava")

        pkg(closureOf<BintrayExtension.PackageConfig> {
            repo = "jar2npm"
            name = "kotlin-jar2npm-plugin"
            desc = "Gradle plugin to extract Kotlin-JS JAR packages to Node node_modules folder to use them in JavaScript projects like React, Angular, etc."
            websiteUrl = "https://github.com/svok/kotlin-multiplatform-sample"
            issueTrackerUrl = "https://github.com/svok/kotlin-multiplatform-sample/issues"
            vcsUrl = "https://github.com/svok/kotlin-multiplatform-sample.git"
            githubRepo = "svok/kotlin-multiplatform-sample"
            githubReleaseNotesFile = "CHANGELOG.md"
            setLicenses("MIT")
            setLabels(
                "kotlin",
                "NodeJs",
                "node",
                "jar",
                "npm",
                "package",
                "JavaScript"
            )
            publish = true
            setPublications("mavenJava")
            version(closureOf<BintrayExtension.VersionConfig> {
                this.name = project.version.toString()
                released = Date().toString()
            })
        })
    }
    publish {
        dependsOn(bintrayUpload)
//        dependsOn(bintrayPublish)
    }

}
