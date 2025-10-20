plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.codemc.io/repository/EvenMoreFish/")
            credentials {
                username = System.getenv("JENKINS_USERNAME")
                password = System.getenv("JENKINS_PASSWORD")
            }
        }
    }
}
