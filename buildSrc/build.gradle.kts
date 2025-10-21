plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

dependencies {
    implementation(files(rootProject.file("gradle/libs.versions.toml")))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
    implementation(libs.plugin.yml)
    implementation(libs.shadow)
    implementation(libs.grgit)
}
repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
    mavenCentral()
}