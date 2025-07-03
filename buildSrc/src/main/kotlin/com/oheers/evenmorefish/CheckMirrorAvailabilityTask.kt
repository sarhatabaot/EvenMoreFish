package com.oheers.evenmorefish

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.net.HttpURLConnection
import java.net.URL


@CacheableTask
abstract class CheckMirrorAvailabilityTask : DefaultTask() {

    @Input
    val mirrorUrl: Property<String> = project.objects.property(String::class.java)

    @Input
    val connectTimeout: Property<Int> = project.objects.property(Int::class.java)

    @Input
    val readTimeout: Property<Int> = project.objects.property(Int::class.java)

    @Classpath
    val mirrorAwareLibs = project.objects.fileCollection()

    @OutputFile
    val outputFile: RegularFile = project.layout.buildDirectory.file("tmp/mirror/missing.txt").get()


    @TaskAction
    fun check() {
        val artifacts = mirrorAwareLibs
            .filter { it.name.endsWith(".jar") }
            .mapNotNull { file ->
                val nameParts = file.name.removeSuffix(".jar").split("-")
                if (nameParts.size >= 2) {
                    val artifactId = nameParts.dropLast(1).joinToString("-")
                    val version = nameParts.last()
                    Triple(file.name, artifactId, version)
                } else null
            }

        val mirror = mirrorUrl.get()
        val missing = mutableListOf<String>()

        artifacts.forEach { (fileName, artifactId, version) ->
            val path = "$mirror/${artifactId.replace('.', '/')}/$version/$fileName"
            try {
                val conn = URL(path).openConnection() as HttpURLConnection
                conn.requestMethod = "HEAD"
                conn.connectTimeout = connectTimeout.get()
                conn.readTimeout = readTimeout.get()
                if (conn.responseCode != 200) missing += path
            } catch (_: Exception) {
                missing += path
            }
        }

        // Write missing paths to file
        val output = outputFile.asFile
        output.writeText(missing.joinToString("\n"))
        logger.lifecycle("✅ Checked ${artifacts.size} artifacts. Missing: ${missing.size}")
    }
}