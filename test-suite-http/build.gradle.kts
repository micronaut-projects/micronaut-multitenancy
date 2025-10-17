plugins {
    `java-library`
}
dependencies {
    testAnnotationProcessor(mn.micronaut.inject.java)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testImplementation(mnTest.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testImplementation(projects.micronautMultitenancy)
    testRuntimeOnly(mnTest.junit.platform.suite)
}
tasks.withType<Test> {
    useJUnitPlatform()
}
