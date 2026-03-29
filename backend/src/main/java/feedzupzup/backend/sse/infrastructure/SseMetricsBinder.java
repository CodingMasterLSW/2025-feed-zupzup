package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.domain.SseEmitterRepository;
import feedzupzup.backend.sse.service.SseTrafficStatePort;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SseMetricsBinder implements MeterBinder {

    public static final String ACTIVE_CONNECTIONS_METRIC_NAME = "sse.connections.active";
    public static final String BLUE_ACTIVE_CONNECTIONS_METRIC_NAME = "blue_active_sse_connections";
    public static final String GREEN_ACTIVE_CONNECTIONS_METRIC_NAME = "green_active_sse_connections";

    private final SseEmitterRepository sseEmitterRepository;
    private final ObjectProvider<SseTrafficStatePort> trafficStatePortProvider;

    public SseMetricsBinder(
            @Qualifier("inMemorySseEmitterRepository") final SseEmitterRepository sseEmitterRepository,
            final ObjectProvider<SseTrafficStatePort> trafficStatePortProvider
    ) {
        this.sseEmitterRepository = sseEmitterRepository;
        this.trafficStatePortProvider = trafficStatePortProvider;
    }

    @Override
    public void bindTo(final MeterRegistry registry) {
        Gauge.builder(ACTIVE_CONNECTIONS_METRIC_NAME, sseEmitterRepository, SseEmitterRepository::count)
                .description("Current number of active SSE connections")
                .register(registry);

        Gauge.builder(BLUE_ACTIVE_CONNECTIONS_METRIC_NAME, this, binder -> binder.countConnectionsFor(SseTrafficState.BLUE))
                .description("Current number of active SSE connections on blue deployment")
                .register(registry);

        Gauge.builder(GREEN_ACTIVE_CONNECTIONS_METRIC_NAME, this, binder -> binder.countConnectionsFor(SseTrafficState.GREEN))
                .description("Current number of active SSE connections on green deployment")
                .register(registry);
    }

    private int countConnectionsFor(final String deploymentColor) {
        final SseTrafficStatePort trafficStatePort = trafficStatePortProvider.getIfAvailable();
        if (trafficStatePort == null) {
            return 0;
        }

        final SseTrafficState trafficState = trafficStatePort.read();
        if (!trafficState.isDeploymentColor(deploymentColor)) {
            return 0;
        }

        return sseEmitterRepository.count();
    }
}
