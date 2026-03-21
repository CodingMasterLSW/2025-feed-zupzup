package feedzupzup.backend.sse.infrastructure;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.sse.dto.FeedbackCountMessage;
import feedzupzup.backend.sse.service.SseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RedisSseSubscriberTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SseService sseService = Mockito.mock(SseService.class);

    @Test
    @DisplayName("Redis 메시지를 받으면 SSE 서비스로 피드백 수 알림을 전달한다")
    void handleMessage() throws Exception {
        final RedisSseSubscriber subscriber = new RedisSseSubscriber(objectMapper, sseService);
        final FeedbackCountMessage feedbackCountMessage = new FeedbackCountMessage(
                1L,
                3L,
                "event-1",
                1_742_545_678_901L
        );
        final String message = objectMapper.writeValueAsString(feedbackCountMessage);

        subscriber.handleMessage(message);

        verify(sseService).sendFeedbackNotificationToOrganization(feedbackCountMessage);
    }
}
