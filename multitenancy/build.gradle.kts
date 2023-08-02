plugins {
    id("io.micronaut.build.internal.multitenancy-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    api(mnValidation.validation)
    api(mn.micronaut.http)
    api(mn.micronaut.inject)

    compileOnly(mnSession.micronaut.session)

    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mnReactor.micronaut.reactor)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mnSession.micronaut.session)
    testImplementation(mn.snakeyaml)
}
