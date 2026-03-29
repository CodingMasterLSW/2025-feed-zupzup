package feedzupzup.backend.sse.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.sse.service.SseTrafficStatePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty("app.deploy-state.path")
public class FileBasedSseTrafficStateAdapter implements SseTrafficStatePort {

    private final ObjectMapper objectMapper;
    private final Path stateFilePath;

    public FileBasedSseTrafficStateAdapter(
            final ObjectMapper objectMapper,
            @Value("${app.deploy-state.path}") final String stateFilePath
    ) {
        this.objectMapper = objectMapper;
        this.stateFilePath = Path.of(stateFilePath);
    }

    @Override
    public SseTrafficState read() {
        if (!Files.exists(stateFilePath)) {
            return SseTrafficState.allowSse();
        }

        try {
            return objectMapper.readValue(stateFilePath.toFile(), SseTrafficState.class);
        } catch (IOException e) {
            log.error("배포 상태 파일을 읽지 못해 SSE 허용 상태로 처리합니다. path={}", stateFilePath, e);
            return SseTrafficState.allowSse();
        }
    }
}
