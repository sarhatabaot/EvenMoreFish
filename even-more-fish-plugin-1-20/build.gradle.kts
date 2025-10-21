plugins {
    id("java")
    id("com.oheers.evenmorefish.plugin-yml-conventions")
    id("com.oheers.evenmorefish.shadow-conventions")
    alias(libs.plugins.run.paper)
}

group = "com.oheers.evenmorefish"

extra["variant"] = "1.20"

dependencies {
    implementation(project(":even-more-fish-plugin")) //rename to core later
    implementation(libs.commandsapi.bukkit)
}

tasks {
    // Quick manual testing, don't use this in ci/cd
    runServer {
        minecraftVersion("1.20.1")
        jvmArgs("-Dcom.mojang.eula.agree=true")
    }

    shadowJar {
        relocate("dev.jorel.commandapi", "com.oheers.fish.libs.commandapi")
    }

}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}