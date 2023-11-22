package io.micronaut.multitenancy.expression

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.core.annotation.Nullable
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.multitenancy.tenantresolver.HttpHeaderTenantResolverConfiguration
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
@Property(name = "spec.name", value = "TenantEvaluationContextSpec")
@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.TRUE)
class TenantEvaluationContextSpec extends Specification {

    @Inject
    @Client("/")
    HttpClient client

    void "test tenant evaluation context"() {
        when:
        def request = HttpRequest.GET("/tenant/expression").header(HttpHeaderTenantResolverConfiguration.DEFAULT_HEADER_NAME, "allowed")
        def response = client.toBlocking().retrieve(request)

        then:
        response == "allowed"

        when:
        request = HttpRequest.GET("/tenant/expression")
        response = client.toBlocking().retrieve(request)

        then:
        response == "none"
    }

    @Controller("/tenant")
    @Requires(property = "spec.name", value = "TenantEvaluationContextSpec")
    static class TestTenantResolver {

        final BeanContext beanContext

        TestTenantResolver(BeanContext beanContext) {
            this.beanContext = beanContext
        }

        @Get("/expression")
        @Produces(MediaType.TEXT_PLAIN)
        String expression() {
            beanContext.createBean(Holder).value
        }
    }

    @Factory
    @Requires(property = "spec.name", value = "TenantEvaluationContextSpec")
    static class TestFactory {

        @Bean
        Holder holder(@Value("#{ tenantId }") @Nullable String tenantId) {
            new Holder(value: tenantId ?: 'none')
        }
    }

    static class Holder {
        String value
    }
}
