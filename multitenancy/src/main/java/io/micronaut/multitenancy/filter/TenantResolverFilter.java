/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.multitenancy.filter;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import org.jspecify.annotations.Nullable;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.filter.FilterPatternStyle;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;
import io.micronaut.multitenancy.tenantresolver.HttpRequestTenantResolver;
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Adds a tenant identifier if resolved as a request attribute.
 * @author Sergio del Amo
 * @since 5.5.0
 */
@ServerFilter(patternStyle = FilterPatternStyle.REGEX,
        value = "${" + TenantResolverFilterConfigurationProperties.PREFIX + ".regex-pattern:" + TenantResolverFilterConfigurationProperties.DEFAULT_REGEX_PATTERN + "}")
@Internal
@Requires(condition = TenantResolverExistsCondition.class)
final class TenantResolverFilter implements Ordered {
    /**
     * Request attribute for tenant Identifier.
     */
    public static final String ATTRIBUTE_TENANT = "tenantIdentifier";

    private static final Logger LOG = LoggerFactory.getLogger(TenantResolverFilter.class);

    @Nullable
    private final HttpRequestTenantResolver httpRequestTenantResolver;

    @Nullable
    private final TenantResolver tenantResolver;

    /**
     *
     * @param httpRequestTenantResolver HTTP Request Tenant Resolver
     * @param tenantResolver Tenant Resolver
     */
    TenantResolverFilter(@Nullable HttpRequestTenantResolver httpRequestTenantResolver,
                         @Nullable TenantResolver tenantResolver) {
        this.httpRequestTenantResolver = httpRequestTenantResolver;
        this.tenantResolver = tenantResolver;
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.before();
    }

    @RequestFilter
    void filter(HttpRequest<?> request) {
        Optional<String> tenantOptional = resolveTenant(request);
        if (tenantOptional.isEmpty()) {
            tenantOptional = resolveTenant();
        }
        tenantOptional.ifPresent(tenant -> request.setAttribute(ATTRIBUTE_TENANT, tenant));
    }

    private Optional<String> resolveTenant(HttpRequest<?> request) {
        if (httpRequestTenantResolver == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(httpRequestTenantResolver.resolveTenantId(request));
        } catch (TenantNotFoundException ex) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Tenant could not be resolved");
            }
        }
        return Optional.empty();
    }

    private Optional<String> resolveTenant() {
        if (tenantResolver == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(tenantResolver.resolveTenantId());
        } catch (TenantNotFoundException ex) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Tenant could not be resolved");
            }
        }
        return Optional.empty();
    }
}

