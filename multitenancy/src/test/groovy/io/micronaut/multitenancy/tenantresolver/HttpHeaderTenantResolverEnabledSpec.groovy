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


import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "micronaut.multitenancy.tenantresolver.httpheader.enabled", value = StringUtils.TRUE)
@MicronautTest(startApplication = false)
class HttpHeaderTenantResolverEnabledSpec extends Specification {
    @Inject
    BeanContext beanContext

    void "TenantResolver is enabled if micronaut.multitenancy.tenantresolver.httpheader.enabled = true"() {
        expect:
        beanContext.containsBean(TenantResolver)
    }
}
