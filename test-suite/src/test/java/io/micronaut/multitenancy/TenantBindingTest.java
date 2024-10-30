package io.micronaut.multitenancy;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "spec.name", value = "TenantBindingTest")
@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.tenant-id", value = "expected")
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.enabled", value = StringUtils.TRUE)
@MicronautTest
class TenantBindingTest {
    @Test
    void tenantBinding(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.GET("/tenant")
                .accept(MediaType.TEXT_PLAIN);
        String tenant = assertDoesNotThrow(() -> client.retrieve(request));
        assertEquals("expected", tenant);
    }
}
