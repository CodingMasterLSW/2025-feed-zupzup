package feedzupzup.backend.sse.event;

import feedzupzup.backend.sse.domain.ConnectionType;

public record SseConnectedEvent(
        ConnectionType connectionType
) {
}
