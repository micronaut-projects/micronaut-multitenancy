
plugins {
    id("io.micronaut.build.internal.multitenancy-module")
}

description = "Expression language support for multi-tenancy"

dependencies {
    compileOnly(mn.micronaut.core.processor)
}

micronautBuild {
    binaryCompatibility {
        enabled = false
    }
}
