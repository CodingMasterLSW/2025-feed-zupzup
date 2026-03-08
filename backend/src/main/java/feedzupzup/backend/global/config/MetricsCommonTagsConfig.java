package feedzupzup.backend.global.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsCommonTagsConfig {

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTagsCustomizer(
            @Value("${app.metrics.instance-id:unknown-instance}") final String instanceId
    ) {
        return registry -> registry.config().commonTags("InstanceId", instanceId);
    }
}
