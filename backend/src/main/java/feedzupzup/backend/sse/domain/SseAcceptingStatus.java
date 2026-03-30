package feedzupzup.backend.sse.domain;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

@Component
public class SseAcceptingStatus {

    private final AtomicBoolean accepting = new AtomicBoolean(true);

    public void stopAccepting() {
        accepting.set(false);
    }

    public void resumeAccepting() {
        accepting.set(true);
    }

    public boolean isAcceptStatus() {
        return accepting.get();
    }

}
