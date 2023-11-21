plugins {
    id("io.micronaut.build.internal.multitenancy-module")
}

dependencies {
    api(mn.micronaut.http)
    compileOnly(mn.micronaut.http.server)
    api(mn.micronaut.inject)
    compileOnly(mnSession.micronaut.session)
    compileOnly(libs.managed.publicsuffixlist)
    compileOnly(libs.guava)
    testImplementation(libs.managed.publicsuffixlist)
    testImplementation(libs.guava)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mnReactor.micronaut.reactor)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mnSession.micronaut.session)
    testImplementation(mn.snakeyaml)
}
