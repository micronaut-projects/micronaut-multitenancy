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
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.util.DefaultHttpHostResolver;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * A tenant resolver that resolves the tenant from the Subdomain.
 *
 * @author Sergio del Amo
 * @since 6.0
 */
@Singleton
@Requires(bean = HttpHostResolver.class)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class SubdomainTenantResolver implements TenantResolver, HttpRequestTenantResolver {

    HttpHostResolver httpHostResolver;

    /**
     * @param httpHostResolver HTTP host resolver.
     * @since 5.0.3
     */
    @Inject
    SubdomainTenantResolver(HttpHostResolver httpHostResolver) {
        this.httpHostResolver = httpHostResolver;
    }

    /**
     * @deprecated Use {@link SubdomainTenantResolver#SubdomainTenantResolver(HttpHostResolver)}
     */
    @Deprecated(since = "5.0.3", forRemoval = true)
    SubdomainTenantResolver() {
        this(new DefaultHttpHostResolver(new HttpServerConfiguration(), null));
    }

    @Override
    @NonNull
    public Serializable resolveTenantIdentifier() {
        Optional<HttpRequest<Object>> current = ServerRequestContext.currentRequest();
        return current.map(this::resolveTenantIdentifier).orElseThrow(() -> new TenantNotFoundException("Tenant could not be resolved outside a web request"));
    }

    @Override
    @NonNull
    public Serializable resolveTenantIdentifier(@NonNull HttpRequest<?> request) throws TenantNotFoundException {
        final String host = httpHostResolver.resolve(Objects.requireNonNull(request, "request must not be null"));
        return resolveIdentifier(normalizeHost(host));
    }

    private String normalizeHost(String host) {
        return host.contains("/") ? host.substring(host.indexOf("/") + 2) : host;
    }

    private Serializable resolveIdentifier(String host) {
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
