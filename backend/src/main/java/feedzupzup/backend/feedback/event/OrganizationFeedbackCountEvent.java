package feedzupzup.backend.feedback.event;

import java.util.UUID;

public record OrganizationFeedbackCountEvent(
        Long organizationId,
        long totalFeedbackCount,
        String eventId,
        long publishedAt
) {

    public static OrganizationFeedbackCountEvent of(
            final Long organizationId,
            final long totalFeedbackCount
    ) {
        return new OrganizationFeedbackCountEvent(
                organizationId,
                totalFeedbackCount,
                UUID.randomUUID().toString(),
                System.currentTimeMillis()
        );
    }
}
