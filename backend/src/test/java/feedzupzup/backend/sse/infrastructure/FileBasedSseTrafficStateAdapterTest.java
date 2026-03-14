package feedzupzup.backend.sse.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileBasedSseTrafficStateAdapterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("상태 파일이 없으면 SSE를 허용한다")
    void allowSseWhenStateFileDoesNotExist() {
        final Path missingFilePath = tempDir.resolve("missing-deploy-state.json");
        final FileBasedSseTrafficStateAdapter stateReader = new FileBasedSseTrafficStateAdapter(
                objectMapper,
                missingFilePath.toString()
        );

        assertThat(stateReader.shouldNotReceiveSse()).isFalse();
    }

    @Test
    @DisplayName("상태 파일의 shouldNotReceiveSse 값을 읽는다")
    void readShouldNotReceiveSseFromStateFile() throws IOException {
        final Path stateFilePath = tempDir.resolve("deploy-state.json");
        Files.writeString(stateFilePath, """
                {
                  "shouldNotReceiveSse": true
                }
                """);
        final FileBasedSseTrafficStateAdapter stateReader = new FileBasedSseTrafficStateAdapter(
                objectMapper,
                stateFilePath.toString()
        );

        assertThat(stateReader.shouldNotReceiveSse()).isTrue();
    }

    @Test
    @DisplayName("상태 파일이 깨져 있으면 SSE를 허용한다")
    void allowSseWhenStateFileIsInvalid() throws IOException {
        final Path stateFilePath = tempDir.resolve("deploy-state.json");
        Files.writeString(stateFilePath, "{ invalid json }");
        final FileBasedSseTrafficStateAdapter stateReader = new FileBasedSseTrafficStateAdapter(
                objectMapper,
                stateFilePath.toString()
        );

        assertThat(stateReader.shouldNotReceiveSse()).isFalse();
    }
}
