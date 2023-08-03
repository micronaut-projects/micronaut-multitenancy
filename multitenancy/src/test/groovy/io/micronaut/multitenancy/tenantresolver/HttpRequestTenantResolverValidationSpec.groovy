package io.micronaut.multitenancy.tenantresolver

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.TRUE)
@MicronautTest(startApplication = false)
class HttpRequestTenantResolverValidationSpec extends Specification {
    @Inject
    HttpRequestTenantResolver tenantResolver

    void "HttpRequestTenantResolver::resolveTenantIdentifier(HttpRequest) validates request is not null"() {
        when:
        tenantResolver.resolveTenantIdentifier(null)

        then:
        NullPointerException e = thrown()
        e.message.contains("request must not be null")
    }
}
