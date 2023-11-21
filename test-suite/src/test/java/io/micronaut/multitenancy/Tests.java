package io.micronaut.multitenancy;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class Tests {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testSecuredAnnotationCanSeeTenantIds() {
        BlockingHttpClient blockingClient = client.toBlocking();

        // Sergio requires authentication as the tenant is not 'allowed'
        MutableHttpRequest<Object> sergio = HttpRequest.GET("/").header("X-Tenant", "sergio");
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> blockingClient.exchange(sergio));
        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());

        // But the allowed request has tenant as allowed so secured annotation evaluates to true and the request is allowed
        MutableHttpRequest<Object> tim = HttpRequest.GET("/").header("X-Tenant", "allowed");
        assertEquals("Hello World", blockingClient.retrieve(tim));
    }
}
