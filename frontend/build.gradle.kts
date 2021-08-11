import com.github.gradle.node.yarn.task.YarnTask

description = "Ships - Frontend"

plugins {
  java  // for the jar task, also includes the build task
  id("com.github.node-gradle.node")
}

tasks.register<YarnTask>("buildFrontend") {
  dependsOn("yarn_install") // need yarn/node to create the frontend
  description = "Builds the frontend into the dist directory"
  args.set(listOf("run", "build"))  // the build job drops everything into build/dist
}

// see: https://fbflex.wordpress.com/2014/03/14/building-web-content-jars-for-spring-boot-with-gradle/
tasks.register<Jar>("webjar") {
  dependsOn("buildFrontend") //
  // build/dist needs to match with the output from the node build script triggered in buildFrontend
  description = "Creates the webjar from the content in the dist folder"
  from(fileTree("build/dist"))
}

// config for the node-gradle plugin
node {
  download.set(true)
  distBaseUrl.set("https://nodejs.org/dist")

  val projectNodeVersion: String by project
  version.set(projectNodeVersion)

  val projectNpmVersion: String by project
  npmVersion.set(projectNpmVersion)

  val projectYarnVersion: String by project
  yarnVersion.set(projectYarnVersion)
}

// config for the maven-publish plugin
// we publish the webjar to include it into the backend module
publishing {
  publications {
    // see: https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#publishing-your-application
    // https://stackoverflow.com/questions/61197984/bootjar-mavenjar-artifact-wasnt-produced-by-this-build
    create<MavenPublication>("frontend") {
      artifact(tasks.getByName("webjar"))
    }
  }
}
