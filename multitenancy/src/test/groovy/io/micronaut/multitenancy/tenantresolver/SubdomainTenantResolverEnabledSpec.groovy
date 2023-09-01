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
import io.micronaut.http.HttpRequest
import io.micronaut.multitenancy.exceptions.TenantNotFoundException
import spock.lang.AutoCleanup
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

    void "TenantResolver resolves tenant identifier"() {
        given:
        HttpRequest<?> request = HttpRequest.GET(uri)
        if (header != null) {
            request = request.header('Host', header)
        }

        expect:
        context.getBean(SubdomainTenantResolver).resolveTenantIdentifier(request) == expected

        where:
        uri                                     | header         | expected
        'https://www.example.com/path/resource' | 'test.com'     | 'test'
        'https://www.example.com/path/resource' | null           | 'www'
        'https://example.com/path/resource'     | null           | 'example'
        'https://www.example.com/path/resource' | ''             | 'DEFAULT'
        'https://www.example.com/path/resource' | 'test'         | 'DEFAULT'
        'https://www.example.com/path/resource' | 'test.com'     | 'test'
        'https://example.com/path/resource'     | 'www.test.com' | 'www'
    }

    void "TenantResolver resolves tenant identifier - no http request"() {
        when:
        new SubdomainTenantResolver().resolveTenantIdentifier()

        then:
        thrown(TenantNotFoundException)
    }
}
