plugins {
    id("io.micronaut.build.internal.multitenancy-module")
}

dependencies {
    compileOnly(mn.micronaut.http.server)
    compileOnly(mnSession.micronaut.session)
    compileOnly(libs.managed.publicsuffixlist)
    compileOnly(libs.guava)

    testImplementation(projects.micronautMultitenancyAnnotations)
    testImplementation(libs.managed.publicsuffixlist)
    testImplementation(libs.guava)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mnReactor.micronaut.reactor)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mnSession.micronaut.session)
    testImplementation(mn.snakeyaml)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(mnTest.junit.jupiter.engine)

    constraints {
        implementation("org.apache.commons:commons-lang3:3.18.0"){
            because("Older versions have security vulnerabilities")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
