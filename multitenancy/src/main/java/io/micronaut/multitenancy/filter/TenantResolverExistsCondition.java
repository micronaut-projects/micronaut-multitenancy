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

import io.micronaut.context.BeanContext;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.Internal;
import io.micronaut.multitenancy.tenantresolver.HttpRequestTenantResolver;
import io.micronaut.multitenancy.tenantresolver.TenantResolver;

/**
 * Condition that evaluates to true if a bean of type {@link HttpRequestTenantResolver} or a {@link TenantResolver} exist in the context.
 * @author Sergio del Amo
 * @since 5.5.0
 */
@Internal
final class TenantResolverExistsCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        BeanContext beanContext = context.getBeanContext();
        return beanContext.containsBean(HttpRequestTenantResolver.class)
                || beanContext.containsBean(TenantResolver.class);
    }
}
