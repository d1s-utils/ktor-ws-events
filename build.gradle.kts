/*
 * Copyright 2022-2023 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.dokka")
    id("com.github.ben-manes.versions")
}

buildscript {
    dependencies {
        val dokkaVersion: String by project

        classpath("org.jetbrains.dokka:dokka-base:$dokkaVersion")
    }
}

allprojects {
    ext {
        set("publishingScript", "${rootProject.projectDir.absolutePath}/publishing.gradle.kts")
    }

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("java-library")
        plugin("org.jetbrains.dokka")
    }

    val projectGroup: String by project
    val projectVersion: String by project

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    group = projectGroup
    version = projectVersion

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.majorVersion
        }
    }

    tasks.withType<DokkaTask> {
        dokkaSourceSets {
            configureEach {
                val moduleDocsPath: String by project

                includes.setFrom(moduleDocsPath)
            }
        }

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "Copyright (c) 2022-2023 Mikhail Titov"
        }
    }

    tasks.withType<DokkaMultiModuleTask> {
        val outputDir = buildDir.resolve("dokkaMultiModule")

        outputDirectory.set(outputDir)
    }


    kotlin {
        explicitApi()
    }
}
