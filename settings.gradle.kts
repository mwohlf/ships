rootProject.name = "ships"
include("backend")
include("frontend")


// see: https://docs.gradle.org/7.0-rc-1/release-notes.html
enableFeaturePreview("VERSION_CATALOGS")

// see: https://docs.gradle.org/current/userguide/platforms.html
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            alias("boot-configuration-processor").to("org.springframework.boot:spring-boot-configuration-processor:2.5.3")
            alias("spring-boot-starter-web").to("org.springframework.boot:spring-boot-starter-web:2.5.3")
            alias("spring-boot-test").to("org.springframework.boot:spring-boot-starter-test:2.5.3")
            alias("spring-boot-cache").to("org.springframework.boot:spring-boot-starter-cache:2.5.3")
            alias("openapi-ui").to("org.springdoc:springdoc-openapi-ui:1.5.10")

            alias("guava").to("com.google.guava:guava:30.1.1-jre")
            alias("lombok").to("org.projectlombok:lombok:1.18.20")

            alias("netty").to("io.netty:netty-buffer:4.1.66.Final")

            alias("commons-csv").to("org.apache.commons:commons-csv:1.9.0")
            alias("postgresql-driver").to("org.postgresql:postgresql:42.2.23")
            alias("modelmapper").to("org.modelmapper.extensions:modelmapper-spring:2.4.4")
            alias("hikari-cp").to("com.zaxxer:HikariCP:5.0.0")
            alias("jackson-databind").to("com.fasterxml.jackson.core:jackson-databind:2.12.4")
            alias("jackson-jsr310").to("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4")
            alias("testcontainers").to("org.testcontainers:testcontainers-bom:1.15.3")
        }
    }
}
