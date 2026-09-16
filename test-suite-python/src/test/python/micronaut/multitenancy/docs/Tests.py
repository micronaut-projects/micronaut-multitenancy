from typing import Annotated

from jakarta.inject import Inject
from micronaut.http import HttpRequest, HttpStatus
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.http.client.exceptions import HttpClientResponseException
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@MicronautTest
class Tests:

    client: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_secured_annotation_can_see_tenant_ids(self):
        blocking_client = self.client.toBlocking()

        # Sergio requires authentication as the tenant is not 'allowed'
        sergio = HttpRequest.GET("/").header("X-Tenant", "sergio")
        try:
            blocking_client.exchange(sergio)
        except HttpClientResponseException as thrown:
            assert thrown.getStatus() == HttpStatus.UNAUTHORIZED
        else:
            assert False, "expected an HttpClientResponseException"

        # But the allowed request has tenant as allowed so secured annotation evaluates to true and the request is allowed
        tim = HttpRequest.GET("/").header("X-Tenant", "allowed")
        assert blocking_client.retrieve(tim) == "Hello World"
