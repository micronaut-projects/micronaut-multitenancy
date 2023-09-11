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

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.multitenancy.exceptions.TenantNotFoundException
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
        context.getBean(TenantResolver)

        then:
        noExceptionThrown()

        and:
        context.containsBean(HttpRequestTenantResolver)
        context.getBean(HttpRequestTenantResolver) instanceof SubdomainTenantResolver
    }

    @PendingFeature
    void "TenantResolver resolves tenant identifier without host header"() {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri)

        expect:
        context.getBean(SubdomainTenantResolver).resolveTenantIdentifier(request) == expected

        and:
        new SubdomainTenantResolver().resolveTenantIdentifier(request) == expected

        where:
        uri                                              | expected
        'https://example.com/path/resource'              | 'DEFAULT'
        'https://www.example.com/path/resource'          | 'www'
        'https://domain.co.uk/path/resource'             | 'DEFAULT'
        'https://duper.domain.co.uk/path/resource'       | 'duper'
        'https://super.duper.domain.co.uk/path/resource' | 'super.duper'
        'https://example.com/path/resource'              | 'DEFAULT'
    }

    void "TenantResolver resolves tenant identifier"(String uri, String header, String expected) {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri).header(HttpHeaders.HOST, header)

        expect:
        context.getBean(SubdomainTenantResolver).resolveTenantIdentifier(request) == expected

        and:
        new SubdomainTenantResolver().resolveTenantIdentifier(request) == expected

        where:
        uri                                              | header                     | expected
        'https://www.example.com/path/resource'          | ''                         | 'DEFAULT'
        'https://www.example.com/path/resource'          | 'test'                     | 'DEFAULT'
        'https://example.com/path/resource'              | 'www.test.com'             | 'www'
        'https://example.com/path/resource'              | 'duper.domain.co.uk'       | 'duper'
        'https://example.com/path/resource'              | 'lorem.ipsum.dev'          | 'lorem'
    }

    @PendingFeature
    void "TenantResolver resolves tenant identifier with header"(String uri, String header, String expected) {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri).header(HttpHeaders.HOST, header)

        expect:
        context.getBean(SubdomainTenantResolver).resolveTenantIdentifier(request) == expected

        and:
        new SubdomainTenantResolver().resolveTenantIdentifier(request) == expected

        where:
        uri                                              | header                     | expected
        'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        'https://www.example.com/path/resource'          | 'test.com'                 | 'DEFAULT'
        'https://example.com/path/resource'              | 'domain.co.uk'             | 'DEFAULT'
        'https://example.com/path/resource'              | 'super.duper.domain.co.uk' | 'super.duper'
    }

    void "TenantResolver resolves tenant identifier - no http request"() {
        when:
        new SubdomainTenantResolver().resolveTenantIdentifier()

        then:
        thrown(TenantNotFoundException)
    }
}
