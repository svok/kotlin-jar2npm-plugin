# Gradle plugin Jar2Npm

This plugin is aimed to extract KotlinJS JAR packages to Node node_modules folder to use them in
JavaScript projects like React, Angular, etc.

## About the project

Kotlin multiplatform uses JAR packages by default as a target build. It brings no problems is your project is targeted to 
the JVM platform. But as you want to build a joined backend-frontend system with a multiplatform module
you get an anoying headacke with convertion of JavaScript JAR packages to the standard NodeJs packagins system. 

There is a promising plugin [Kotlin Frontend Plugin](https://github.com/Kotlin/kotlin-frontend-plugin) which
is developed by [Jetbrains](https://www.jetbrains.com/) to overcome the denoted problem. But it looks abandoned: there 
are no updates for couple monthes, several merge requests are waiting for acception for monthes, there are plenty 
unclosed issues and annoying bugs.

Such a situation motivated us to publish another gradle plugin for kotlin-based 
JS frameworks: [kotlin-jar2npm-plugin](https://github.com/svok/kotlin-jar2npm-plugin).
This plugin extracts the content of the KotlinJS JAR-packages to node_modules package repository
and allows KotlinJS code available in all other JavaScript projects.

This plugin is build with the usage of [Moowork Node Gradle plugin](https://plugins.gradle.org/plugin/com.moowork.node).
So, you can keep your own `package.json` in the root of your KotlinJS project and use all the power of `NodeJS`.

## Use in your projects

In `build.gradle.kts`:
```kotlin
plugins {
  id("com.crowdproj.plugins.jar2npm") version "0.1.15"
}
```

In `settings.bradle.kts`:
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { setUrl("https://dl.bintray.com/svok/jar2npm") }
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.crowdproj.plugins.jar2npm" -> useModule("com.crowdproj.plugins:jar2npm-plugin:${requested.version}")
            }
        }
    }
}
```

## Example

For an example see [Sample multiplatform and multi-frontend Kotlin project](https://github.com/svok/kotlin-multiplatform-sample)
especially [Reactfront module](https://github.com/svok/kotlin-multiplatform-sample/tree/master/proj-reactfront). 
