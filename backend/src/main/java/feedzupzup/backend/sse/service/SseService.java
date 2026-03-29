package feedzupzup.backend.sse.service;

import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.domain.SseAcceptingStatus;
import feedzupzup.backend.sse.domain.SseEmitterRepository;
import feedzupzup.backend.sse.dto.FeedbackCountMessage;
import feedzupzup.backend.sse.event.SseConnectedEvent;
import feedzupzup.backend.sse.exception.SseException.SseConnectionRefusedException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final OrganizationRepository organizationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SseAcceptingStatus sseAcceptingStatus;

    public SseService(
            @Qualifier("inMemorySseEmitterRepository")
            final SseEmitterRepository sseEmitterRepository,
            final OrganizationRepository organizationRepository,
            final ApplicationEventPublisher eventPublisher,
            final SseAcceptingStatus sseAcceptingStatus
    ) {
        this.sseEmitterRepository = sseEmitterRepository;
        this.organizationRepository = organizationRepository;
        this.eventPublisher = eventPublisher;
        this.sseAcceptingStatus = sseAcceptingStatus;
    }

    public SseEmitter createEmitter(
            final UUID organizationUuid,
            final String userId,
            final ConnectionType connectionType
    ) {
        if (!sseAcceptingStatus.isAcceptStatus()) {
            throw new SseConnectionRefusedException("현재 SSE 연결을 받을 수 없는 상태입니다.");
        }
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        final Organization organization = organizationRepository.findByUuid(organizationUuid)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 조직 UUID 입니다."));

        final String emitterId = generateEmitterId(organization.getId(), userId, connectionType);

        sseEmitterRepository.save(emitterId, emitter);
        log.info("SSE 연결 생성 - Type: {}, Emitter ID: {}", connectionType, emitterId);
        eventPublisher.publishEvent(new SseConnectedEvent(connectionType));

        emitter.onCompletion(() -> {
            log.info("SSE 연결 정상 종료 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onError((e) -> {
            final String message = e.getMessage();
            if (message != null && message.contains("disconnected client")) {
                log.debug("SSE 연결 종료(Client Disconnect)");
            } else {
                log.error("SSE 연결 에러 발생 - ID: {}, 메시지: {}", emitterId, message, e);
            }
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE 연결 타임아웃 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        sendToClient(emitter, emitterId, "connect", "EventStream Created");

        return emitter;
    }

    public void sendFeedbackNotificationToOrganization(final FeedbackCountMessage feedbackCountMessage) {
        final Long organizationId = feedbackCountMessage.organizationId();
        final Map<String, SseEmitter> sseEmitters = sseEmitterRepository.findAllByOrganizationId(
                organizationId);
        log.info("피드백 수 전송 시작 - Organization: {}", organizationId);

        if (sseEmitters.isEmpty()) {
            log.info("전송 대상 연결 없음 - Organization: {}", organizationId);
            return;
        }

        log.info("전송 대상 연결 수: {}", sseEmitters.size());
        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, SseEmitter> entry : sseEmitters.entrySet()) {
            final String emitterId = entry.getKey();
            final SseEmitter emitter = entry.getValue();

            try {
                emitter.send(SseEmitter.event()
                        .id(feedbackCountMessage.eventId())
                        .name("feedback-total-count-notification")
                        .data(feedbackCountMessage));
                successCount ++;
            } catch (IOException e) {
                log.warn("피드백 수 전송 실패 - Emitter: {}, 원인: {}", emitterId, e.getMessage());
                sseEmitterRepository.remove(emitterId);
                failCount++;
            }
        }
        log.info("피드백 수 전송 완료 - Organization: {}, 성공: {}, 실패: {}",
                organizationId, successCount, failCount);
    }

    public void resumeAccepting() {
        sseAcceptingStatus.resumeAccepting();
    }

    @EventListener(ContextClosedEvent.class)
    public void completeAllEmitters() {
        sseAcceptingStatus.stopAccepting();
        final Map<String, SseEmitter> allEmitters = sseEmitterRepository.findAll();
        log.info("SSE 연결 전체 해제 시작 - {} 개", allEmitters.size());

        for (String key : allEmitters.keySet()) {
            final SseEmitter emitter = allEmitters.get(key);
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("이미 끊긴 SSE 연결 - {}: {}", key, e.getMessage());
            }
            sseEmitterRepository.remove(key);
        }
        log.info("SSE 연결 전체 해제 완료");
    }

    private void sendToClient(final SseEmitter emitter, final String id, final String eventName, final Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            sseEmitterRepository.remove(id);
            log.error("SSE 연결오류 발생", e);
        }
    }

    private String generateEmitterId(
            final Long organizationId,
            final String userId,
            final ConnectionType connectionType
    ) {
        return organizationId + "_"
                + connectionType.getPrefix() + "_"
                + userId + "_"
                + System.currentTimeMillis();
    }
}
