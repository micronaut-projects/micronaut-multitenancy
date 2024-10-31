package io.micronaut.multitenancy.filter;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
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

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Property(name = "micronaut.multitenancy.filter.regex-pattern", value = "^(?!\\/assets).*$")
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.tenant-id", value = "expected")
@Property(name = "micronaut.multitenancy.tenantresolver.fixed.enabled", value = StringUtils.TRUE)
@Property(name = "spec.name", value = "TenantResolverFilterPatternTest")
@MicronautTest
class TenantResolverFilterPatternTest {

    @Test
    void tenantFilterRegexDoesNotMatch(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.GET("/assets/images/logo.png")
                .accept(MediaType.TEXT_PLAIN);
        String tenant = assertDoesNotThrow(() -> client.retrieve(request));
        assertEquals("no tenant", tenant);
    }

    @Test
    void tenantFilterRegexMatches(@Client("/") HttpClient httpClient) {
        BlockingHttpClient client = httpClient.toBlocking();
        String tenant = assertDoesNotThrow(() -> client.retrieve(HttpRequest.GET("/tenant")
                .accept(MediaType.TEXT_PLAIN)));
        assertEquals("expected", tenant);
    }

    @Requires(property = "spec.name", value = "TenantResolverFilterPatternTest")
    @Controller("/assets")
    static class AssetsController {

        @Produces(MediaType.TEXT_PLAIN)
        @Get("/images/logo.png")
        String echoTenant(@Nullable Tenant<Serializable> tenant) {
            return tenant != null ? tenant.id().toString() : "no tenant";
        }
    }

    @Requires(property = "spec.name", value = "TenantResolverFilterPatternTest")
    @Controller("/tenant")
    static class TenantController {

        @Produces(MediaType.TEXT_PLAIN)
        @Get
        String echoTenant(Tenant<Serializable> tenant) {
            return tenant.id().toString();
        }
    }

}