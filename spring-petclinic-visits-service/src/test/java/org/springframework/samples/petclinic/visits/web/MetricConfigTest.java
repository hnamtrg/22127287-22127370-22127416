package org.springframework.samples.petclinic.visits.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetricConfigTest {

    private final MetricConfig metricConfig = new MetricConfig();

    @Test
    void metricsCommonTagsShouldAddApplicationTag() {
        // Given
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        MeterRegistryCustomizer<MeterRegistry> customizer = metricConfig.metricsCommonTags();
        
        // When
        customizer.customize(meterRegistry);
        
        // Then
        // Để kiểm tra tag, tạo một meter và xác minh rằng nó có tag "application=petclinic"
        Meter meter = meterRegistry.counter("test.counter");
        boolean hasTag = false;
        for (Meter.Id id : meterRegistry.getMeters().stream().map(Meter::getId).toList()) {
            if (id.getTag("application") != null && id.getTag("application").equals("petclinic")) {
                hasTag = true;
                break;
            }
        }
        assertTrue(hasTag, "Meter should have tag 'application=petclinic'");
    }

    @Test
    void timedAspectShouldBeCreated() {
        // Given
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        
        // When
        TimedAspect timedAspect = metricConfig.timedAspect(meterRegistry);
        
        // Then
        assertNotNull(timedAspect, "TimedAspect should not be null");
    }
}