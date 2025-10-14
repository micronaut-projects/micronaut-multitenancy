package io.micronaut.multitenancy.filter;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class TenantResolverFilterConfigurationTest {

    @Test
    void defaultEnabled(TenantResolverFilterConfiguration tenantResolverFilterConfiguration) {
        assertTrue(tenantResolverFilterConfiguration.isEnabled());
    }
}