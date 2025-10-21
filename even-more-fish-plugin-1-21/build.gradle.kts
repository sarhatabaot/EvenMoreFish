plugins {
    id("java")
    id("com.oheers.evenmorefish.plugin-yml-conventions")
    alias(libs.plugins.run.paper)
}

group = "com.oheers.evenmorefish"

extra["variant"] = "1.21"

dependencies {
    implementation(project(":even-more-fish-plugin")) //rename to core later
    implementation(libs.commandsapi.paper)
}
tasks {
    // Quick manual testing, don't use this in ci/cd
    runServer {
        minecraftVersion("1.21.10")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}
tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}