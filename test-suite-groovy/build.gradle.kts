plugins {
    id("groovy")
}

description = "Test Suite for testing and documenting the Multitenancy features"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(projects.micronautMultitenancyAnnotations)
    testImplementation(projects.micronautMultitenancy)

    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mnSecurity.micronaut.security.annotations)

    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.jackson.databind)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnTest.micronaut.test.spock)

    testRuntimeOnly(mn.snakeyaml)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
