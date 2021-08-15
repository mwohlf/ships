import org.springframework.boot.gradle.tasks.bundling.BootJar;

description = "Ships - Backend"

plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}

// this task depends on the frontend parts we want to include into the final artifact
tasks.named("compileJava") {  // from the java plugin
    // we need the frontend webjar in the maven repo
    dependsOn(":frontend:publishToMavenLocal")
}

tasks.named<BootJar>("bootJar") {
    dependsOn("compileJava") //
    archiveFileName.set("fat.jar")
}

// see https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md for the Docker template
configure<com.google.cloud.tools.jib.gradle.JibExtension> {
    val baseImage: String by project
    val imageName: String by project
    from.image = baseImage
    to.image = "${imageName}"  // ships
    container.creationTime = "USE_CURRENT_TIMESTAMP"
    container.ports = kotlin.collections.listOf("8080")
    container.jvmFlags = kotlin.collections.listOf(
        "-Djava.security.egd=file:/dev/urandom",
        "-Duser.timezone=\"Europe/Berlin\"",
        "-Dhttps.protocols=\"TLSv1.2\"",
    )
}

configure<org.springframework.boot.gradle.dsl.SpringBootExtension> {
    // see: https://stackoverflow.com/questions/58488499/gradle-not-recognizing-springboot-buildinfo-plugin
    buildInfo()
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.cache) // TODO
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(libs.openapi.ui)
    implementation(libs.hikari.cp)
    implementation(libs.postgresql.driver)
    implementation(libs.commons.csv)
    implementation(libs.netty)
    implementation("com.squareup.okhttp:okhttp:2.7.5")
    implementation("com.google.code.findbugs:jsr305:3.0.2")


    implementation(libs.jackson.jsr310)

    compileOnly(libs.lombok)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.boot.configuration.processor)

    // ---- testing
    testImplementation(libs.spring.boot.test)
    implementation(platform("org.testcontainers:testcontainers-bom:1.15.3")) //import bom
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.3.1")

    // ---- the frontend
    implementation("net.wohlfart.ships:frontend:0.0.1-SNAPSHOT")
}


// config for the maven-publish plugin
publishing {
    publications {
        create<MavenPublication>("ships") {
            artifact(tasks.getByName("bootJar"))
        }
    }
}


