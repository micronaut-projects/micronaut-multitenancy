package io.micronaut.multitenancy;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest;
import jakarta.inject.Inject
import spock.lang.Specification;

@MicronautTest
class Tests extends Specification {

    @Inject
    @Client("/")
    HttpClient client;

    void testSecuredAnnotationCanSeeTenantIds() {
        when:
        MutableHttpRequest<Object> sergio = HttpRequest.GET("/").header("X-Tenant", "sergio")
        client.toBlocking().exchange(sergio)

        then:
        def exception = thrown(HttpClientResponseException)
        exception.status == HttpStatus.UNAUTHORIZED

        when:
        // But the tim request has tenant as tim so secured annotation evaluates to false
        MutableHttpRequest<Object> tim = HttpRequest.GET("/").header("X-Tenant", "allowed")

        then:
        client.toBlocking().retrieve(tim) == "Hello World"
    }
}
