plugins {
    idea
    base
    // just to nail the versions for the subprojects, we don't apply them in the root:
    id("org.springframework.boot").version("2.5.3").apply(false)
    id("com.github.node-gradle.node").version("3.1.0").apply(false)
    id("com.google.cloud.tools.jib").version("3.1.3").apply(false)
}

allprojects {
    val projectGroup: String by project
    val projectVersion: String by project
    group = projectGroup
    version = projectVersion

    tasks.withType<JavaCompile>() {
        options.encoding = "UTF-8"
        sourceCompatibility = "15"
        targetCompatibility = "15"
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.spring.io/snapshot")
        maven(url = "https://repo.spring.io/milestone")
        maven(url = "https://repo.maven.apache.org/maven2/")
    }

    // see: https://docs.gradle.org/current/userguide/publishing_maven.html
    apply(plugin = "maven-publish")

    tasks.create<Delete>("mrproper") {
        dependsOn("clean")
        description = """
            Cleanup anything that might be downloaded or created, trying to reset the project to the checkout state,
            this task is shared with every module
        """
        delete = setOf (
            ".node", "node_modules", "target", "build", "yarn.lock",
            "guided-tests/js/guided-test-runtime.js", "yarn-error.log",
            "package-lock.json", "src/generated", "openapitools.json"
        )
    }

}

tasks.register<DefaultTask>("info") {
    println("-- some commandlines for this project --")
    println("   * gradle build: create the artifacts")
    println("   * gradle publishToMavenLocal: publish artifacts")
    println("   * gradle bootRun: start the springboot application")
    println("   * gradle buildImage: create the docker image")
    println("   * docker run -p 8080:8080 ships:latest   -- to run the created docker image")
}
