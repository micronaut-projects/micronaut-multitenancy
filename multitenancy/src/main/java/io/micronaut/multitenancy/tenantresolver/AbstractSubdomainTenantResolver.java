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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract class for a tenant resolver that resolves the tenant from the Subdomain.
 *
 * @author Sergio del Amo
 * @since 5.1.0
 */
public abstract class AbstractSubdomainTenantResolver implements TenantResolver, HttpRequestTenantResolver {
    private static final String HTTPS_SLASH_SLASH = "https://";
    private static final String HTTP_SLASH_SLASH = "http://";

    /**
     * Http Host resolver.
     */
    protected HttpHostResolver httpHostResolver;

    /**
     * @param httpHostResolver HTTP host resolver.
     */
    protected AbstractSubdomainTenantResolver(HttpHostResolver httpHostResolver) {
        this.httpHostResolver = httpHostResolver;
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
        return resolveSubdomain(hostWithoutProtocol(host));
    }

    /**
     *
     * @param host Host
     * @return The subdomain.
     */
    @NonNull
    protected abstract Serializable resolveSubdomain(@NonNull String host);

    @NonNull
    private String hostWithoutProtocol(@NonNull String host) {
        final int protocol = host.indexOf("://");
        return protocol < 0 ? host : host.substring(protocol + 3);
    }
}
