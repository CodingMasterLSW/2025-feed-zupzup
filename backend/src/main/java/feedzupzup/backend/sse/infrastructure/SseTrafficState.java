package feedzupzup.backend.sse.infrastructure;

public record SseTrafficState(
        boolean shouldNotReceiveSse
) {

    public static SseTrafficState allowSse() {
        return new SseTrafficState(false);
    }
}
