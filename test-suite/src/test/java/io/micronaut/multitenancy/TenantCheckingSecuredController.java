package io.micronaut.multitenancy;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;

// tag::clazz[]
@Controller
public class TenantCheckingSecuredController {

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    @Secured("#{ tenantId == 'allowed' }") // <1>
    public String index() {
        return "Hello World";
    }
}
// end::clazz[]
