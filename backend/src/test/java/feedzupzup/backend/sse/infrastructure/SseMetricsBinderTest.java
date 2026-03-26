package feedzupzup.backend.sse.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
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
        final SseMetricsBinder sseMetricsBinder = new SseMetricsBinder(
                sseEmitterRepository,
                objectProvider(null)
        );

        // when
        sseMetricsBinder.bindTo(meterRegistry);

        // then
        final Gauge gauge = meterRegistry.get(SseMetricsBinder.ACTIVE_CONNECTIONS_METRIC_NAME).gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("blue 배포 인스턴스면 blue 메트릭에만 활성 SSE 연결 수를 노출한다")
    void bindBlueActiveConnectionsGauge() {
        final InMemorySseEmitterRepository sseEmitterRepository = new InMemorySseEmitterRepository();
        sseEmitterRepository.save("1_GUEST_user-a_1000", new SseEmitter());
        sseEmitterRepository.save("1_ADMIN_user-b_1001", new SseEmitter());

        final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        final SseMetricsBinder sseMetricsBinder = new SseMetricsBinder(
                sseEmitterRepository,
                objectProvider(() -> new SseTrafficState(SseTrafficState.BLUE, false))
        );

        sseMetricsBinder.bindTo(meterRegistry);

        final Gauge blueGauge = meterRegistry.get(SseMetricsBinder.BLUE_ACTIVE_CONNECTIONS_METRIC_NAME).gauge();
        final Gauge greenGauge = meterRegistry.get(SseMetricsBinder.GREEN_ACTIVE_CONNECTIONS_METRIC_NAME).gauge();

        assertThat(blueGauge.value()).isEqualTo(2.0);
        assertThat(greenGauge.value()).isZero();
    }

    @Test
    @DisplayName("green 배포 인스턴스면 green 메트릭에만 활성 SSE 연결 수를 노출한다")
    void bindGreenActiveConnectionsGauge() {
        final InMemorySseEmitterRepository sseEmitterRepository = new InMemorySseEmitterRepository();
        sseEmitterRepository.save("1_GUEST_user-a_1000", new SseEmitter());

        final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        final SseMetricsBinder sseMetricsBinder = new SseMetricsBinder(
                sseEmitterRepository,
                objectProvider(() -> new SseTrafficState(SseTrafficState.GREEN, false))
        );

        sseMetricsBinder.bindTo(meterRegistry);

        final Gauge blueGauge = meterRegistry.get(SseMetricsBinder.BLUE_ACTIVE_CONNECTIONS_METRIC_NAME).gauge();
        final Gauge greenGauge = meterRegistry.get(SseMetricsBinder.GREEN_ACTIVE_CONNECTIONS_METRIC_NAME).gauge();

        assertThat(blueGauge.value()).isZero();
        assertThat(greenGauge.value()).isEqualTo(1.0);
    }

    private static ObjectProvider<feedzupzup.backend.sse.service.SseTrafficStatePort> objectProvider(
            final feedzupzup.backend.sse.service.SseTrafficStatePort trafficStatePort
    ) {
        return new ObjectProvider<>() {
            @Override
            public feedzupzup.backend.sse.service.SseTrafficStatePort getObject(final Object... args) {
                return trafficStatePort;
            }

            @Override
            public feedzupzup.backend.sse.service.SseTrafficStatePort getIfAvailable() {
                return trafficStatePort;
            }

            @Override
            public feedzupzup.backend.sse.service.SseTrafficStatePort getIfUnique() {
                return trafficStatePort;
            }

            @Override
            public feedzupzup.backend.sse.service.SseTrafficStatePort getObject() {
                return trafficStatePort;
            }
        };
    }
}
