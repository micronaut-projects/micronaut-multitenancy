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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Internal;
import io.micronaut.multitenancy.MultitenancyConfiguration;

/**
 * {@link ConfigurationProperties} implementation of {@link TenantResolverFilterConfiguration}.
 */
@ConfigurationProperties(TenantResolverFilterConfigurationProperties.PREFIX)
@Internal
final class TenantResolverFilterConfigurationProperties implements TenantResolverFilterConfiguration {
    public static final String PREFIX = MultitenancyConfiguration.PREFIX + ".filter";
    public static final String PROPERTY_ENABLED = PREFIX + ".enabled";
    /**
     * The default regex pattern.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_REGEX_PATTERN = "^.*$";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;
    private String regexPattern = DEFAULT_REGEX_PATTERN;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Whether {@link io.micronaut.multitenancy.filter.TenantResolverFilter} should be enabled. Default value ({@value #DEFAULT_ENABLED}).
     * @param enabled enabled flag
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getRegexPattern() {
        return regexPattern;
    }

    /**
     * Tenant Resolver filter processes only request paths matching this regular expression. Default Value: {@value #DEFAULT_REGEX_PATTERN}
     * @param regexPattern Regular expression pattern for the filter.
     */
    public void setRegexPattern(String regexPattern) {
        this.regexPattern = regexPattern;
    }
}
