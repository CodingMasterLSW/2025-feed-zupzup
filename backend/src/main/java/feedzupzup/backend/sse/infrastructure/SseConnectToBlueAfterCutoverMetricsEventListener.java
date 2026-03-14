package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.event.SseConnectedEvent;
import feedzupzup.backend.sse.service.SseTrafficStatePort;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseConnectToBlueAfterCutoverMetricsEventListener {

    public static final String METRIC_NAME = "sse.connect.to.blue.after.cutover";

    private final MeterRegistry meterRegistry;
    private final ObjectProvider<SseTrafficStatePort> trafficStatePortProvider;

    @Async
    @EventListener
    public void handle(final SseConnectedEvent event) {
        final SseTrafficStatePort trafficStatePort = trafficStatePortProvider.getIfAvailable();
        if (trafficStatePort == null || !trafficStatePort.shouldNotReceiveSse()) {
            return;
        }

        meterRegistry.counter(
                METRIC_NAME,
                "connectionType", event.connectionType().name().toLowerCase()
        ).increment();
    }
}
