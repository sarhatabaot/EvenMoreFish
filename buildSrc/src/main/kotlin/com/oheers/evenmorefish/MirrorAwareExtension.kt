package com.oheers.evenmorefish

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class MirrorAwareExtension @Inject constructor(objects: ObjectFactory) {
    val mirrorUrl: Property<String> = objects.property(String::class.java).convention(
        "https://maven-central.storage-download.googleapis.com/maven2"
    )

    val connectionTimeout: Property<Int> = objects.property(Int::class.java).convention(5000)
    val readTimeout: Property<Int> = objects.property(Int::class.java).convention(5000)

    val relocationPrefix: Property<String> = objects.property(String::class.java).convention(
        "com.oheers.evenmorefish.shaded"
    )
}