package feedzupzup.backend.sse.service;

import feedzupzup.backend.sse.infrastructure.SseTrafficState;

public interface SseTrafficStatePort {

    SseTrafficState read();

    default boolean shouldNotReceiveSse() {
        return read().shouldNotReceiveSse();
    }
}
