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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A tenant resolver that resolves the tenant from the Subdomain.
 *
 * @author Sergio del Amo
 * @since 6.0
 */
@Singleton
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
        this.httpHostResolver = null;
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
        Objects.requireNonNull(request, "request must not be null");
        return Optional.ofNullable(httpHostResolver)
            .map(r -> r.resolve(request))
            .or(() -> getHost(request))
            .map(this::normalizeHost)
            .map(this::resolveIdentifier)
            .orElse(TenantResolver.DEFAULT);
    }

    private Optional<String> getHost(@NonNull HttpRequest<?> request) {
        return Optional.ofNullable(request.getHeaders().get(HttpHeaders.HOST));
    }

    private String normalizeHost(String host) {
        return host.contains("/") ? host.substring(host.indexOf("/") + 2) : host;
    }

    private Serializable resolveIdentifier(String host) {
        return host.contains(".") ? host.substring(0, host.indexOf(".")) : null;
    }
}
