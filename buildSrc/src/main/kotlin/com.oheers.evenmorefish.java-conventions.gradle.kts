plugins {
    id("java")
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

tasks {

    if (project.name.contains("addons")) {
        jar {
            val addonName = defaultAddonName(project.name, project.version.toString())
            archiveFileName.set(addonName)

            // probably should extract this part into it's own plugin.
            manifest {
                val buildNumber: String? by project

                attributes["name"] = project.name
                attributes["version"] = project.version
                attributes["description"] = project.description ?: ""
                attributes["author"] = project.properties["author"] ?: "" //todo test for now like this
            }
        }

    }
}

fun defaultAddonName(project: String, addonVersion: String): String {
    val jvmVersion = project.split("-")[4].uppercase()
    return "EMF-Addons-${jvmVersion}-${addonVersion}.addon"
}

