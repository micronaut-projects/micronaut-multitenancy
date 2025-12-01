package io.micronaut.multitenancy.filter;


import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import org.jspecify.annotations.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.multitenancy.Tenant;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "spec.name", value = "TenantTypedRequestArgumentBinderNoBindingTest")
@MicronautTest
class TenantTypedRequestArgumentBinderNoBindingTest {

    @Test
    void tenantBinding(@Client("/")HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.GET("/tenant")
                .accept(MediaType.TEXT_PLAIN);
        String tenant = assertDoesNotThrow(() -> client.retrieve(request));
        assertEquals("no tenant", tenant);
    }

    @Requires(property = "spec.name", value = "TenantTypedRequestArgumentBinderNoBindingTest")
    @Controller("/tenant")
    static class TenantController {

        @Produces(MediaType.TEXT_PLAIN)
        @Get
        String echoTenant(@Nullable Tenant tenant) {
            return tenant != null ? tenant.id().toString() : "no tenant";
        }
    }
}