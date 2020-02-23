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

    kotlin("jvm") apply false
    id("com.gradle.plugin-publish") apply false
    id("com.jfrog.bintray") apply false
    id("org.jetbrains.dokka") apply false
    id("net.nemerosa.versioning") apply false
}

group = "com.crowdproj.plugins"
version = "1.0.3"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        jcenter()
        mavenCentral()
    }
}
