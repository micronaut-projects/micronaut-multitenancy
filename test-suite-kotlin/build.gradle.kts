plugins {
    id("io.micronaut.build.internal.kotlin-kapt")
}

description = "Test Suite for testing and documenting the Multitenancy features"

repositories {
    mavenCentral()
}

dependencies {
    kaptTest(mn.micronaut.inject.kotlin)
    kaptTest(projects.micronautMultitenancyAnnotations)
    kaptTest(mnSecurity.micronaut.security.annotations)

    testImplementation(projects.micronautMultitenancy)

    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.inject.java)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.jackson.databind)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSecurity.micronaut.security.annotations)

    testImplementation(mnTest.micronaut.test.junit5)

    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testRuntimeOnly(mnTest.junit.platform.suite)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
