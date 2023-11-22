package io.micronaut.multitenancy.propagation.cookie

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.cookie.Cookie
import io.micronaut.multitenancy.propagation.TenantPropagationHttpClientFilter
import io.micronaut.multitenancy.tenantresolver.CookieTenantResolver
import io.micronaut.multitenancy.tenantresolver.HttpRequestTenantResolver
import io.micronaut.multitenancy.tenantresolver.TenantResolver
import io.micronaut.multitenancy.writer.CookieTenantWriter
import io.micronaut.multitenancy.writer.TenantWriter
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Specification

class CookieTenantResolverSpec extends Specification {

    @AutoCleanup
    EmbeddedServer gormEmbeddedServer = ApplicationContext.run(EmbeddedServer, [
    'spec.name'                                           : 'multitenancy.cookie.gorm',
    'micronaut.multitenancy.tenantresolver.cookie.enabled': true
    ], Environment.TEST)

    @AutoCleanup
    HttpClient gormClient = gormEmbeddedServer.applicationContext.createBean(HttpClient, gormEmbeddedServer.URL)

    @AutoCleanup
    EmbeddedServer gatewayEmbeddedServer = ApplicationContext.run(EmbeddedServer, [
            'spec.name'                                           : 'multitenancy.cookie.gateway',
            'micronaut.http.services.books.url'                   : "http://localhost:${gormEmbeddedServer.port}",
            'micronaut.multitenancy.propagation.enabled'          : true,
            'micronaut.multitenancy.propagation.service-id-regex' : 'books',
            'micronaut.multitenancy.tenantwriter.cookie.enabled'  : true,
            'micronaut.multitenancy.tenantresolver.cookie.enabled': true
    ])

    @AutoCleanup
    HttpClient gatewayClient = gatewayEmbeddedServer.applicationContext.createBean(HttpClient, gatewayEmbeddedServer.URL)

    def "setup gorm server"() {
        when:
        for (Class beanClazz : [BookService, BooksController, Bootstrap]) {
            gormEmbeddedServer.applicationContext.getBean(beanClazz)
        }

        then:
        noExceptionThrown()

        and:
        gormEmbeddedServer.applicationContext.containsBean(TenantResolver)
        gormEmbeddedServer.applicationContext.getBean(TenantResolver) instanceof CookieTenantResolver
        gormEmbeddedServer.applicationContext.containsBean(HttpRequestTenantResolver)
        gormEmbeddedServer.applicationContext.getBean(HttpRequestTenantResolver) instanceof CookieTenantResolver
    }

    def "setup gateway server"() {
        when:
        for (Class beanClazz : [
                GatewayController,
                BooksClient,
                CookieTenantWriter,
                CookieTenantResolver,
                TenantWriter,
                TenantResolver,
                TenantPropagationHttpClientFilter
        ]) {
            gatewayEmbeddedServer.applicationContext.getBean(beanClazz)
        }

        then:
        noExceptionThrown()
    }

    def "fetch books for watson and sherlock directly from the books microservice, the tenantId is in the HTTP Header. They get only their books"() {
        when:
        HttpResponse rsp = gormClient.toBlocking().exchange(HttpRequest.GET('/api/books')
                .cookie(Cookie.of("tenantId", "sherlock")), Argument.of(List, String))

        then:
        rsp.status() == HttpStatus.OK
        rsp.body().size() == 1
        ['Sherlock diary'] == rsp.body()

        when:
        rsp = gormClient.toBlocking().exchange(HttpRequest.GET('/api/books')
                .cookie(Cookie.of("tenantId", "watson")), Argument.of(List, String))

        then:
        rsp.status() == HttpStatus.OK
        rsp.body().size() == 1
        ['Watson diary'] == rsp.body()
    }

    def "fetch books for watson and sherlock, since the tenant ID is in the HTTP header and its propagated. They get only their books"() {
        when:
        def getWithCookie = HttpRequest.GET('/').cookie(Cookie.of("tenantId", "sherlock"))
        HttpResponse<List<String>> rsp = gatewayClient.toBlocking().exchange(getWithCookie, Argument.listOf(String))

        then:
        rsp.status() == HttpStatus.OK
        rsp.body().size() == 1
        ['Sherlock diary'] == rsp.body()

        when:
        rsp = gatewayClient.toBlocking().exchange(HttpRequest.GET('/')
                .cookie(Cookie.of("tenantId", "watson")), Argument.of(List, String))

        then:
        rsp.status() == HttpStatus.OK
        rsp.body().size() == 1
        ['Watson diary'] == rsp.body()
    }
}
