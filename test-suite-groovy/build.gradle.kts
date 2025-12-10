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
    testRuntimeOnly(mnTest.junit.platform.suite)

    testRuntimeOnly(mnLogging.logback.classic)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
//TODO remove once Micronaut Test ships Spock version compatible with Groovy 5
configurations.all {
    resolutionStrategy {
        force("org.spockframework:spock-core:2.4-M7-groovy-5.0")
    }
}
