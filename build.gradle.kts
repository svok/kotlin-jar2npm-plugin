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

    kotlin("jvm") version kotlin_version apply false
    id("com.gradle.plugin-publish") version "0.10.1" apply false
    id("com.jfrog.bintray") version "1.8.4" apply false
    id("org.jetbrains.dokka") version "0.9.17" apply false
    id("net.nemerosa.versioning") version "2.8.2" apply false
}

group = "com.crowdproj.plugins"
version = "1.0.1"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        jcenter()
        mavenCentral()
    }
}
