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

import com.google.common.net.InternetDomainName;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.server.util.HttpHostResolver;
import jakarta.inject.Singleton;
import java.io.Serializable;
import static java.util.stream.Collectors.joining;

/**
 * A tenant resolver that resolves the tenant from the subdomain. To resolve the subdomain it uses Guava {@link com.google.common.net.InternetDomainName}.
 *
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Singleton
@Requires(bean = HttpHostResolver.class)
@Requires(classes = InternetDomainName.class)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".guava.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class InternetDomainNameSubdomainTenantResolver extends AbstractSubdomainTenantResolver {
    /**
     * @param httpHostResolver HTTP host resolver.
     */
    public InternetDomainNameSubdomainTenantResolver(HttpHostResolver httpHostResolver) {
        super(httpHostResolver);
    }

    @Override
    @NonNull
    protected Serializable resolveSubdomain(@NonNull String host) {
        if (InternetDomainName.isValid(host)) {
            final InternetDomainName domain = InternetDomainName.from(host);
            final int subdomainParts = domain.parts().size() - getTopDomain(domain).parts().size();
            if (subdomainParts > 0) {
                return domain.parts().stream().limit(subdomainParts).collect(joining("."));
            }
        }
        return DEFAULT;
    }

    private InternetDomainName getTopDomain(InternetDomainName domain) {
        return domain.isUnderPublicSuffix() ? domain.topPrivateDomain() : domain;
    }
}
