package feedzupzup.backend.sse.dto;

public record FeedbackCountMessage(
        Long organizationId,
        long totalFeedbackCount
) {

}
