plugins {
    id("java-library")
    id("io.micronaut.build.internal.python")
}

description = "Test Suite for testing and documenting the Multitenancy features"

repositories {
    mavenCentral()
}

dependencies {
    // The Python compiler (micronaut-inject-python) takes the (jar-resolved) compile classpath as its
    // annotation processor path, so the processors are testImplementation (not testAnnotationProcessor).
    testImplementation(mn.micronaut.inject.python.test)
    testImplementation(mn.micronaut.context.python)
    testImplementation(projects.micronautMultitenancyAnnotations)

    testImplementation(projects.micronautMultitenancy)

    testImplementation(mn.micronaut.http.client)
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
    systemProperty("micronaut.python.pool.enabled", "false")
    // a Truffle host-interop assertion trips on varargs overloads
    enableAssertions = false
}
