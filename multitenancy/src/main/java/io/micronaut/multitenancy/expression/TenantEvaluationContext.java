/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.multitenancy.expression;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.multitenancy.exceptions.TenantNotFoundException;
import io.micronaut.multitenancy.tenantresolver.TenantResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Context for supporting annotation expressions with tenant id.
 *
 * @since 5.2.0
 * @author Tim Yates
 */
@Internal
@Singleton
@Requires(beans = {TenantResolver.class})
public final class TenantEvaluationContext {

    private static final Logger LOG = LoggerFactory.getLogger(TenantEvaluationContext.class);

    private final TenantResolver tenantResolver;

    /**
     * @param tenantResolver The enabled and configured TenantResolver
     */
    public TenantEvaluationContext(TenantResolver tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    /**
     * @return the tenant id or {@literal null}.
     */
    @Nullable
    public String getTenantId() {
        try {
            Serializable tenant = tenantResolver.resolveTenantIdentifier();
            if (tenant instanceof CharSequence charSequenceTenant) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Resolved tenant: {}", tenant);
                }
                return charSequenceTenant.toString();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tenant not resolvable to a String: {}", tenant);
            }
            return null;
        } catch (TenantNotFoundException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tenant not found: {}", ex.getMessage());
            }
            return null;
        }
    }
}
