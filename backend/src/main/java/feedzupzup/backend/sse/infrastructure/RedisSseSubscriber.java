package feedzupzup.backend.sse.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.sse.dto.FeedbackCountMessage;
import feedzupzup.backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSseSubscriber {

    private final ObjectMapper objectMapper;
    private final SseService sseService;

    public void handleMessage(final String message) {
        try {
            final FeedbackCountMessage feedbackCountMessage = objectMapper.readValue(message, FeedbackCountMessage.class);
            sseService.sendFeedbackNotificationToOrganization(
                    feedbackCountMessage.organizationId(),
                    feedbackCountMessage.totalFeedbackCount()
            );
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패 - message: {}", message, e);
        }
    }
}
