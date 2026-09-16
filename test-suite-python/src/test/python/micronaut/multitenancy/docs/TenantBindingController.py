from micronaut.http import MediaType
from micronaut.http.annotation import Controller, Get, Produces
from micronaut.multitenancy import Tenant
from micronaut.security.annotation import Secured
from micronaut.security.rules import SecurityRule


@Controller("/tenant")
class TenantBindingController:

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Produces(MediaType.TEXT_PLAIN)
    @Get
    def echo_tenant(self, tenant: Tenant) -> str:
        return tenant.id()
