/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.multitenancy.tenantresolver;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.util.DefaultHttpHostResolver;
import io.micronaut.http.server.util.HttpHostResolver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.Serializable;

/**
 * Tenant resolver that resolves the tenant from the Subdomain. It requires a bean of type {@link HttpHostResolver}.
 * It does not support every domain suffix. For example, it does not support second-level domains.
 * @see <a href="https://publicsuffix.org">publicsuffix.org</a>
 *
 * @author Sergio del Amo
 */
@Singleton
@Secondary
@Requires(bean = HttpHostResolver.class)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class SubdomainTenantResolver extends AbstractSubdomainTenantResolver {
    private static final char DOT = '.';

    /**
     * @param httpHostResolver HTTP host resolver.
     * @since 5.0.3
     */
    @Inject
    public SubdomainTenantResolver(HttpHostResolver httpHostResolver) {
        super(httpHostResolver);
    }

    /**
     * @deprecated Use {@link SubdomainTenantResolver#SubdomainTenantResolver(HttpHostResolver)}
     */
    @Deprecated(since = "5.0.3", forRemoval = true)
    public SubdomainTenantResolver() {
        this(new DefaultHttpHostResolver(new HttpServerConfiguration(), null));
    }

    @Override
    @NonNull
    protected Serializable resolveSubdomain(@NonNull String host) {
        if (host.chars().filter(ch -> ch == DOT).count() > 1) {
            String domainWithoutSuffix = host.substring(0, host.lastIndexOf(DOT));
            int index = domainWithoutSuffix.lastIndexOf(DOT);
            if (index != -1) {
                return domainWithoutSuffix.substring(0, index);
            }
        }
        return TenantResolver.DEFAULT;
    }
}
