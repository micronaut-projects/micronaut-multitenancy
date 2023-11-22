package io.micronaut.multitenancy

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class Tests {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun testSecuredAnnotationCanSeeTenantIds() {
        // Sergio requires authentication as the tenant is not 'allowed'
        val sergio = HttpRequest.GET<Any>("/").header("X-Tenant", "sergio")
        val thrown = Assertions.assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange<Any, Any>(sergio)
        }
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, thrown.status)

        // But this request has tenant as 'allowed' so secured annotation evaluates to true and the request is allowed
        val tim = HttpRequest.GET<Any>("/").header("X-Tenant", "allowed")
        Assertions.assertEquals("Hello World", client.toBlocking().retrieve(tim))
    }
}
