package feedzupzup.backend.internal.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.sse.service.SseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class InternalControllerTest {

    @Mock
    private SseService sseService;

    @InjectMocks
    private InternalController internalController;

    @Test
    @DisplayName("SSE 전체 연결 해제 요청 시 completeAllEmitters를 호출하고 200 OK를 반환한다")
    void disconnectAllSse() {
        // When
        ResponseEntity<Void> response = internalController.disconnectAllSse();

        // Then
        verify(sseService).completeAllEmitters();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}