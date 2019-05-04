import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlin_version: String by project

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlin_version))
    }
}


plugins {
    val kotlin_version: String by project

//    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version kotlin_version
    id("com.gradle.plugin-publish") version "0.10.1"
}

repositories {
    mavenCentral()
    jcenter()
}

group = "com.crowdproj.plugins"
version = "1.0-SNAPSHOT"

val kotlin_version: String by project
val junitVersion: String by project

dependencies {
    implementation(gradleApi())
    testImplementation(gradleTestKit())
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.moowork.gradle:gradle-node-plugin:1.3.1")
    implementation(kotlin("gradle-plugin"))

    testImplementation(kotlin("test"))
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
        create("jar2npm") {
            id = "com.example.jar2npm"
            displayName = "Jar2Npm"
            description = "Plugin to extract Kotlin JAR JS packages to node_modules folder"
            implementationClass = "com.example.jar2npm.KotlinJar2NpmPlugin"
        }
    }
}

