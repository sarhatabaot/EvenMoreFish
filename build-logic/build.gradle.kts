plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("emfDependencies") {
            id = "com.oheers.emf.dependencies"
            implementationClass = "dependencies-conventions"
        }
        register("emfPublishing") {
            id = "com.oheers.emf.publishing"
            implementationClass = "publishing-conventions"
        }
    }
}