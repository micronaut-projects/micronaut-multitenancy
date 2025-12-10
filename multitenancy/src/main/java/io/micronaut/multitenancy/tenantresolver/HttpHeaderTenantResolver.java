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
import org.jspecify.annotations.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;
import jakarta.inject.Singleton;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link io.micronaut.multitenancy.tenantresolver.TenantResolver} that resolves the tenant from the request HTTP Header.
 *
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Singleton
@Requires(beans = HttpHeaderTenantResolverConfiguration.class)
@Requires(property = HttpHeaderTenantResolverConfigurationProperties.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class HttpHeaderTenantResolver implements TenantResolver, HttpRequestTenantResolver {

    /**
     * The name of the header.
     */
    protected String headerName = HttpHeaderTenantResolverConfiguration.DEFAULT_HEADER_NAME;

    /**
     *
     * @param configuration {@link HttpHeaderTenantResolverConfiguration} configuration
     */
    public HttpHeaderTenantResolver(HttpHeaderTenantResolverConfiguration configuration) {
        if (configuration != null) {
            this.headerName = configuration.getHeaderName();
        }
    }

    /**
     *
     * @return the tenant ID if resolved.
     * @throws TenantNotFoundException if tenant not found
     */
    @Override
    @NonNull
    public String resolveTenantId() throws TenantNotFoundException {
        Optional<HttpRequest<Object>> current = ServerRequestContext.currentRequest();
        return current.map(this::resolveTenantId).orElseThrow(() -> new TenantNotFoundException("Tenant could not be resolved outside a web request"));
    }

    @Override
    @NonNull
    public String resolveTenantId(@NonNull HttpRequest<?> request) throws TenantNotFoundException {
        String tenantId = Objects.requireNonNull(request, "request must not be null").getHeaders().get(headerName);
        if (tenantId == null) {
            throw new TenantNotFoundException("Tenant could not be resolved. Header " + headerName + " value is null");
        }
        return tenantId;
    }

    /**
     *
     * @return the tenant ID if resolved.
     * @throws TenantNotFoundException if tenant not found
     * @deprecated Use {@link #resolveTenantId()} instead
     */
    @Override
    @Deprecated(forRemoval = true, since = "5.5.0")
    @NonNull
    public Serializable resolveTenantIdentifier() throws TenantNotFoundException {
        return resolveTenantId();
    }

    @Override
    @Deprecated(forRemoval = true, since = "5.5.0")
    public Serializable resolveTenantIdentifier(@NonNull HttpRequest<?> request) throws TenantNotFoundException {
        return resolveTenantId(request);
    }
}
