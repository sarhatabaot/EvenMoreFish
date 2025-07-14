plugins {
    id("com.oheers.evenmorefish.java-conventions")
}

version = "1.1.0"

repositories {
    maven("https://repo.nexomc.com/releases/")
    maven("https://repo.oraxen.com/releases/")
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.nexo)
    compileOnly(libs.oraxen)
    compileOnly(libs.bundles.craftengine)
    compileOnly(project(":even-more-fish-api"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}
