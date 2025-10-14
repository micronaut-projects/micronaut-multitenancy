plugins {
    `java-library`
}
dependencies {
    testAnnotationProcessor(mn.micronaut.inject.java)
    testRuntimeOnly(mnLogging.logback.classic)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(mnTest.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testImplementation(projects.micronautMultitenancy)
}
tasks.withType<Test> {
    useJUnitPlatform()
}
