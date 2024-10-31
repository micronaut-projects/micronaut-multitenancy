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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import io.micronaut.multitenancy.Tenant;
import jakarta.inject.Singleton;

import java.io.Serializable;
import java.util.Optional;

/**
 * {@link TypedRequestArgumentBinder} for {@link Tenant}.
 * @author Sergio del Amo
 * @since 5.5.0
 */
@Internal
@Singleton
final class TenantTypedRequestArgumentBinder implements TypedRequestArgumentBinder<Tenant> {
    @Override
    public Argument<Tenant> argumentType() {
        return Argument.of(Tenant.class);
    }

    @Override
    public BindingResult<Tenant> bind(ArgumentConversionContext<Tenant> context, HttpRequest<?> source) {
        if (!source.getAttributes().contains(TenantResolverFilter.ATTRIBUTE_TENANT)) {
            return BindingResult.UNSATISFIED;
        }
        Optional<Serializable> serializableOptional = source.getAttribute(TenantResolverFilter.ATTRIBUTE_TENANT, Serializable.class);
        if (serializableOptional.isEmpty()) {
            return BindingResult.EMPTY;
        }
        Serializable serializable = serializableOptional.get();
        Tenant<Serializable> tenant = () -> serializable;
        return () -> Optional.of(tenant);
    }
}
