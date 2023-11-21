package io.micronaut.multitenancy

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured

// tag::clazz[]
@Controller
class TenantCheckingSecuredController {

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    @Secured("#{ tenantId == 'allowed' }") // <1>
    fun index() = "Hello World"
}
// end::clazz[]
