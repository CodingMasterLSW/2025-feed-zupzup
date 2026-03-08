package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.domain.SseEmitterRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SseMetricsBinder implements MeterBinder {

    public static final String ACTIVE_CONNECTIONS_METRIC_NAME = "sse.connections.active";

    private final SseEmitterRepository sseEmitterRepository;

    public SseMetricsBinder(@Qualifier("inMemorySseEmitterRepository") final SseEmitterRepository sseEmitterRepository) {
        this.sseEmitterRepository = sseEmitterRepository;
    }

    @Override
    public void bindTo(final MeterRegistry registry) {
        Gauge.builder(ACTIVE_CONNECTIONS_METRIC_NAME, sseEmitterRepository, SseEmitterRepository::count)
                .description("Current number of active SSE connections")
                .register(registry);
    }
}
