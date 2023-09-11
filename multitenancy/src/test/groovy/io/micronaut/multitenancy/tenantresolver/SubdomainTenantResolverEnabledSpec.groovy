/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.multitenancy.tenantresolver

import io.micronaut.http.HttpHeaders
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpRequest
import io.micronaut.inject.BeanDefinition
import io.micronaut.multitenancy.exceptions.TenantNotFoundException
import io.micronaut.context.annotation.Secondary
import spock.lang.AutoCleanup
import spock.lang.PendingFeature
import spock.lang.Shared
import spock.lang.Specification

class SubdomainTenantResolverEnabledSpec extends Specification {
    static final SPEC_NAME_PROPERTY = 'spec.name'

    @Shared
    @AutoCleanup ApplicationContext context = ApplicationContext.run([
            'micronaut.multitenancy.tenantresolver.subdomain.enabled': true,
            (SPEC_NAME_PROPERTY):getClass().simpleName
    ], Environment.TEST)

    void "TenantResolver is enabled if micronaut.multitenancy.tenantresolver.subdomain.enabled = true"() {
        when:
        List<TenantResolver> resolvers = context.getBeansOfType(TenantResolver)

        then:
        noExceptionThrown()
        resolvers.stream().any(resolver -> resolver instanceof HttpRequestTenantResolver)
        resolvers.stream().any(resolver -> resolver instanceof InternetDomainNameSubdomainTenantResolver)
        resolvers.stream().any(resolver -> resolver instanceof PublicSuffixListSubdomainTenantResolver)
    }

    @PendingFeature
    void "TenantResolver resolves tenant identifier with Second-level domain"(Class<? extends AbstractSubdomainTenantResolver> clazz, String uri, String header, String expected) {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri)
        if (header != null) {
            request = request.header(HttpHeaders.HOST, header)
        }

        expect:
        context.getBean(clazz).resolveTenantIdentifier(request) == expected

        where:
        clazz                   | uri                                              | header                     | expected
        SubdomainTenantResolver | 'https://domain.co.uk/path/resource'             | null                       | 'DEFAULT'
        SubdomainTenantResolver | 'https://duper.domain.co.uk/path/resource'       | null                       | 'duper'
        SubdomainTenantResolver | 'https://super.duper.domain.co.uk/path/resource' | null                       | 'super.duper'
        SubdomainTenantResolver | 'https://example.com/path/resource'              | 'domain.co.uk'             | 'DEFAULT'
        SubdomainTenantResolver | 'https://example.com/path/resource'              | 'duper.domain.co.uk'       | 'duper'
        SubdomainTenantResolver | 'https://example.com/path/resource'              | 'super.duper.domain.co.uk' | 'super.duper'
    }

    void "TenantResolver resolves tenant identifier"(Class<? extends AbstractSubdomainTenantResolver> clazz, String uri, String header, String expected) {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri)
        if (header != null) {
            request = request.header(HttpHeaders.HOST, header)
        }

        expect:
        context.getBean(clazz).resolveTenantIdentifier(request) == expected

        where:
        clazz                                     | uri                                              | header                     | expected
        SubdomainTenantResolver                   | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://www.example.com/path/resource'          | null                       | 'www'
        SubdomainTenantResolver                   | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://www.example.com/path/resource'          | ''                         | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://www.example.com/path/resource'          | 'test'                     | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        SubdomainTenantResolver                   | 'https://example.com/path/resource'              | 'www.test.com'             | 'www'
        SubdomainTenantResolver                   | 'https://example.com/path/resource'              | 'lorem.ipsum.dev'          | 'lorem'
        InternetDomainNameSubdomainTenantResolver | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://www.example.com/path/resource'          | null                       | 'www'
        InternetDomainNameSubdomainTenantResolver | 'https://domain.co.uk/path/resource'             | null                       | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://duper.domain.co.uk/path/resource'       | null                       | 'duper'
        InternetDomainNameSubdomainTenantResolver | 'https://super.duper.domain.co.uk/path/resource' | null                       | 'super.duper'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://www.example.com/path/resource'          | ''                         | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://www.example.com/path/resource'          | 'test'                     | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | 'www.test.com'             | 'www'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | 'domain.co.uk'             | 'DEFAULT'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | 'duper.domain.co.uk'       | 'duper'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | 'super.duper.domain.co.uk' | 'super.duper'
        InternetDomainNameSubdomainTenantResolver | 'https://example.com/path/resource'              | 'lorem.ipsum.dev'          | 'lorem'
        PublicSuffixListSubdomainTenantResolver   | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://www.example.com/path/resource'          | null                       | 'www'
        PublicSuffixListSubdomainTenantResolver   | 'https://domain.co.uk/path/resource'             | null                       | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://duper.domain.co.uk/path/resource'       | null                       | 'duper'
        PublicSuffixListSubdomainTenantResolver   | 'https://super.duper.domain.co.uk/path/resource' | null                       | 'super.duper'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | null                       | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://www.example.com/path/resource'          | ''                         | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://www.example.com/path/resource'          | 'test'                     | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | 'www.test.com'             | 'www'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | 'domain.co.uk'             | 'DEFAULT'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | 'duper.domain.co.uk'       | 'duper'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | 'super.duper.domain.co.uk' | 'super.duper'
        PublicSuffixListSubdomainTenantResolver   | 'https://example.com/path/resource'              | 'lorem.ipsum.dev'          | 'lorem'
    }

    void "SubdomainTenantResolver is annotated with @Secondary"() {
        when:
        BeanDefinition bd = context.getBeanDefinition(SubdomainTenantResolver)

        then:
        bd.hasAnnotation(Secondary.class)
    }

    void "TenantResolver resolves tenant identifier - no http request"() {
        when:
        context.getBean(InternetDomainNameSubdomainTenantResolver).resolveTenantIdentifier()

        then:
        thrown(TenantNotFoundException)

        when:
        context.getBean(PublicSuffixListSubdomainTenantResolver).resolveTenantIdentifier()

        then:
        thrown(TenantNotFoundException)

        when:
        context.getBean(SubdomainTenantResolver).resolveTenantIdentifier()

        then:
        thrown(TenantNotFoundException)
    }
}
