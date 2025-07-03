plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    `java-gradle-plugin`
}

val kotlinGradlePluginVersion = "2.2.0";
val shadowGradlePluginVersion = "9.0.0-rc1";

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinGradlePluginVersion}")
    compileOnly("com.gradleup.shadow:shadow-gradle-plugin:${shadowGradlePluginVersion}")
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("mirror-aware-plugin") {
            id = "com.oheers.evenmorefish.mirroraware"
            implementationClass = "com.oheers.evenmorefish.MirrorAwarePlugin"
            version = "0.1.0"
        }
    }
}