package feedzupzup.backend.feedback.application.event;

import feedzupzup.backend.feedback.event.OrganizationFeedbackCountEvent;
import feedzupzup.backend.global.message.MessagePublisher;
import feedzupzup.backend.organization.domain.FeedbackAmount;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.sse.dto.FeedbackCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationFeedbackCountHandler {

    private static final String FEEDBACK_COUNT_TOPIC = "sse:feedback-count";

    private final OrganizationStatisticRepository organizationStatisticRepository;
    private final MessagePublisher messagePublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleFeedbackCreatedEvent(final OrganizationFeedbackCountEvent organizationFeedbackCountEvent) {
        try {
            final FeedbackAmount feedbackAmount = organizationStatisticRepository.findFeedbackAmountByOrganizationId(
                    organizationFeedbackCountEvent.organizationId());

            final FeedbackCountMessage message = new FeedbackCountMessage(
                    organizationFeedbackCountEvent.organizationId(),
                    feedbackAmount.getFeedbackTotalCount()
            );

            messagePublisher.publish(FEEDBACK_COUNT_TOPIC, message);
        } catch (Exception e) {
            log.error("피드백 실시간 알림 비동기 처리 중 예외 발생 - OrganizationId : {}", organizationFeedbackCountEvent.organizationId(), e);
        }
    }
}
