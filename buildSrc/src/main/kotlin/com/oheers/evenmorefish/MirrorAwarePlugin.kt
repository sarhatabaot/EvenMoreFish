package com.oheers.evenmorefish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskProvider
import java.net.HttpURLConnection
import java.net.URL

class MirrorAwarePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("mirrorAware", MirrorAwareExtension::class.java)

        val mirrorAwareLibs = project.configurations.create("mirrorAwareLibs") {
            isCanBeResolved = true
            isCanBeConsumed = false
            extendsFrom(project.configurations.getByName("runtimeOnly"))
        }

        val checkMirrorTask: TaskProvider<CheckMirrorAvailabilityTask> =
            project.tasks.register("checkMirrorAvailability", CheckMirrorAvailabilityTask::class.java) {
                this.group = "verification"
                this.description = "Checks availability of mirrorAwareLibs on the mirror"

                this.mirrorUrl.set(extension.mirrorUrl)
                this.connectTimeout.set(extension.connectionTimeout)
                this.readTimeout.set(extension.readTimeout)
                this.mirrorAwareLibs.from(mirrorAwareLibs)
            }
        
        project.afterEvaluate {
            val mirrorUrl = extension.mirrorUrl.get()
            val connectTimeout = extension.connectionTimeout.get()
            val readTimeout = extension.readTimeout.get()
            val relocationPrefix = extension.relocationPrefix.get()

            if (!mirrorAwareLibs.isCanBeResolved) {
                project.logger.warn("⚠️ mirrorAwareLibs is not resolvable. Skipping mirror check.")
                return@afterEvaluate
            }

            if (mirrorAwareLibs.dependencies.isEmpty()) {
                project.logger.lifecycle("ℹ️ No dependencies in mirrorAwareLibs — nothing to check.")
                return@afterEvaluate
            }

            val artifacts = mirrorAwareLibs.resolvedConfiguration.resolvedArtifacts
            val missing = checkArtifactAvailability(
                artifacts = artifacts,
                mirrorUrl = mirrorUrl,
                connectTimeout = connectTimeout,
                readTimeout = readTimeout,
                logger = project.logger
            )


            if (missing.isEmpty()) {
                project.logger.lifecycle("✅ All mirrorAwareLibs are available on the mirror.")
                // Gradle 8+: Add each dependency individually instead of the configuration
                mirrorAwareLibs.dependencies.forEach { dep ->
                    project.dependencies.add("library", dep)
                }
            } else {
                project.logger.warn("⚠️ Some artifacts are missing on the mirror. Falling back to shading:")
                missing.forEach { project.logger.warn(" - $it") }

                // Add all dependencies to implementation
                mirrorAwareLibs.dependencies.forEach { dep ->
                    project.dependencies.add("implementation", dep)
                }

                project.tasks.withType(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java).configureEach {
                    val shadowTask = this
                    // Add dynamic libs to ShadowJar inputs
                    shadowTask.configurations.addAll(mirrorAwareLibs)

                    // Add relocations using public API
                    artifacts.map { it.moduleVersion.id.group to it.name }
                        .distinct()
                        .forEach { (group, artifactId) ->
                            val pattern = group // this must match package base in the actual JARs
                            val relocated = "$relocationPrefix.$artifactId"

                            val relocator = com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator(
                                pattern,
                                relocated,
                                null, // includes (null = include all)
                                null, // excludes
                                false  // raw bytecode matcher
                            )

                            shadowTask.relocate(relocator)
                            project.logger.lifecycle("✅ Registered relocation: $pattern → $relocated")
                        }
                }
            }

            project.logger.lifecycle("📦 Mirror-aware resolution complete.")
        }
    }

    private fun checkArtifactAvailability(
        artifacts: Set<ResolvedArtifact>,
        mirrorUrl: String,
        connectTimeout: Int = 3000,
        readTimeout: Int = 3000,
        logger: Logger? = null // Optional Gradle logger (can be passed from the plugin)
    ): List<String> {
        val missing = mutableListOf<String>()

        artifacts.forEach { artifact ->
            val module = artifact.moduleVersion.id
            val groupPath = module.group.replace('.', '/')
            val fileName = "${artifact.name}-${module.version}.${artifact.extension}"
            val url = "$mirrorUrl/$groupPath/${artifact.name}/${module.version}/$fileName"

            logger?.debug("Checking artifact availability at: $url")

            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "HEAD"
                conn.connectTimeout = connectTimeout
                conn.readTimeout = readTimeout

                val responseCode = conn.responseCode
                if (responseCode != 200) {
                    logger?.warn("Artifact not available (HTTP $responseCode): $url")
                    missing += url
                } else {
                    logger?.debug("Artifact available: $url")
                }
            } catch (e: Exception) {
                logger?.warn("Failed to check artifact ($url): ${e.message}")
                missing += url
            }
        }

        return missing
    }
}
