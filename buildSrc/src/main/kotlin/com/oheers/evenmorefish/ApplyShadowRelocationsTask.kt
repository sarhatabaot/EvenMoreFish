package com.oheers.evenmorefish

import com.github.jengelman.gradle.plugins.shadow.relocation.SimpleRelocator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
abstract class ApplyShadowRelocationsTask : DefaultTask() {

    @get:Input
    abstract val relocationPrefix: Property<String>

    @Classpath
    val mirrorAwareLibs = project.objects.fileCollection()

    @get:InputFile
    abstract val missingFile: RegularFileProperty

    @TaskAction
    fun applyRelocations() {
        val missing: List<String> = missingFile.asFile.get().takeIf { it.exists() }
            ?.readLines()
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        if (missing.isEmpty()) {
            logger.lifecycle("✅ No missing artifacts. No relocations applied.")
            return
        }

        logger.lifecycle("⚠️ Missing artifacts detected; applying relocations and shading.")

        val artifacts = mirrorAwareLibs.resolve().mapNotNull {
            project.dependencies.create(it.name)
        }

        project.tasks.withType(ShadowJar::class.java).configureEach {
            val shadowJar = this
            configurations.addAll(mirrorAwareLibs)

            // Fetch resolved artifacts to relocate
            val resolvedArtifacts = mirrorAwareLibs.resolvedConfiguration.resolvedArtifacts

            resolvedArtifacts
                .filter { it.moduleVersion.id.group.isNotBlank() && it.name.isNotBlank() }
                .distinctBy { it.moduleVersion.id.group + ":" + it.name }
                .forEach { artifact ->
                    val group = artifact.moduleVersion.id.group
                    val artifactId = artifact.name
                    val relocated = "${relocationPrefix.get()}.$artifactId"

                    shadowJar.relocate(
                        SimpleRelocator(
                            group,
                            relocated,
                            null,
                            listOf("META-INF/**", "LICENSE", "LICENSE.txt"),
                            false
                        )
                    )
                    logger.lifecycle("🔀 Relocating $group → $relocated")
                }
        }
    }
}
