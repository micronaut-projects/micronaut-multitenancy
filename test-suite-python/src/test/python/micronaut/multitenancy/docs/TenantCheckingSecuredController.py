from micronaut.http import MediaType
from micronaut.http.annotation import Controller, Get, Produces
from micronaut.security.annotation import Secured


# tag::clazz[]
@Controller
class TenantCheckingSecuredController:

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    @Secured("#{ tenantId == 'allowed' }")  # <1>
    def index(self) -> str:
        return "Hello World"
# end::clazz[]
