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
package io.micronaut.multitenancy.tenantresolver;

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList;
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.server.util.HttpHostResolver;
import jakarta.inject.Singleton;
import java.io.Serializable;


/**
 * A tenant resolver that resolves the tenant from the subdomain. To resolve the subdomain it uses the Public Suffix Library class {@link de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList}.
 *
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Singleton
@Requires(classes = PublicSuffixList.class)
@Requires(bean = HttpHostResolver.class)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".publicsuffixlist.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Requires(property = SubdomainTenantResolverConfigurationProperties.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class PublicSuffixListSubdomainTenantResolver extends AbstractSubdomainTenantResolver {
    private final PublicSuffixList suffixList;

    /**
     *
     * @param httpHostResolver Http Host Resolver
     */
    PublicSuffixListSubdomainTenantResolver(HttpHostResolver httpHostResolver) {
        super(httpHostResolver);
        PublicSuffixListFactory factory = new PublicSuffixListFactory();
        this.suffixList = factory.build();
    }

    @Override
    @NonNull
    protected Serializable resolveSubdomain(@NonNull String host) {
        String domain = suffixList.getRegistrableDomain(host);
        if (domain != null) {
            int index = host.lastIndexOf("." + domain);
            if (index != -1) {
                return host.substring(0, index);
            }
        }
        return TenantResolver.DEFAULT;
    }
}
