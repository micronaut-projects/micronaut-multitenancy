package io.micronaut.multitenancy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "spec.name", value = "TenantBindingTest")
@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.tenant-id", value = "expected")
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.enabled", value = StringUtils.TRUE)
@MicronautTest
class TenantBindingTest extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    void tenantBinding() {
        given:
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        HttpRequest<?> request = HttpRequest.GET("/tenant")
                .accept(MediaType.TEXT_PLAIN)
        String result = client.retrieve(request)

        then:
        "expected" == result
    }
}
