/*
 * Copyright (c) 2023 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("../build-kotlin")
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        val libs by creating {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    id("kotlin-version-catalog")
}

rootProject.name = "testtool-server"

includeBuild("../build-parameters") {
    dependencySubstitution {
        substitute(module("build:build-parameters")).using(project(":"))
    }
}