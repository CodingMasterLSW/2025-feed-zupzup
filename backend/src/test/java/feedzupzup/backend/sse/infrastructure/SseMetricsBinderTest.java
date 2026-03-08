package feedzupzup.backend.sse.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseMetricsBinderTest {

    @Test
    @DisplayName("현재 활성 SSE 연결 수를 Gauge 메트릭으로 노출한다")
    void bindActiveConnectionsGauge() {
        // given
        final InMemorySseEmitterRepository sseEmitterRepository = new InMemorySseEmitterRepository();
        sseEmitterRepository.save("1_GUEST_user-a_1000", new SseEmitter());
        sseEmitterRepository.save("1_ADMIN_user-b_1001", new SseEmitter());

        final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        final SseMetricsBinder sseMetricsBinder = new SseMetricsBinder(sseEmitterRepository);

        // when
        sseMetricsBinder.bindTo(meterRegistry);

        // then
        final Gauge gauge = meterRegistry.get(SseMetricsBinder.ACTIVE_CONNECTIONS_METRIC_NAME).gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(2.0);
    }
}
