from typing import Annotated

from jakarta.inject import Inject
from micronaut.context.annotation import Property
from micronaut.http import HttpRequest, MediaType
from micronaut.http.client import HttpClient
from micronaut.http.client.annotation import Client
from micronaut.test.extensions.junit5.annotation import MicronautTest
from org.junit.jupiter.api import Test


@Property(name="spec.name", value="TenantBindingTest")
@Property(name="micronaut.multitenancy.tenantresolver.httpheader.enabled", value="false")
@Property(name="micronaut.multitenancy.tenantresolver.fixed.tenant-id", value="expected")
@Property(name="micronaut.multitenancy.tenantresolver.fixed.enabled", value="true")
@MicronautTest
class TenantBindingTest:

    http_client: Annotated[HttpClient, Inject, Client("/")]

    @Test
    def test_tenant_binding(self):
        client = self.http_client.toBlocking()
        request = HttpRequest.GET("/tenant").accept(MediaType.TEXT_PLAIN)
        tenant = client.retrieve(request)
        assert tenant == "expected"
