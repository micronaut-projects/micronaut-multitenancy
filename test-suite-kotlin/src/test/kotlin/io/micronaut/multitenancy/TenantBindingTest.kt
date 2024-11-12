package io.micronaut.multitenancy

import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Property(name = "spec.name", value = "TenantBindingTest")
@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.tenant-id", value = "expected")
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.enabled", value = StringUtils.TRUE)
@MicronautTest
internal class TenantBindingTest {
    @Test
    fun tenantBinding(@Client("/") httpClient: HttpClient) {
        val client = httpClient.toBlocking()
        val request: HttpRequest<*> = HttpRequest.GET<Any>("/tenant")
            .accept(MediaType.TEXT_PLAIN)
        val tenant = Assertions.assertDoesNotThrow<String> { client.retrieve(request) }
        Assertions.assertEquals("expected", tenant)
    }
}