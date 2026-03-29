package feedzupzup.backend.sse.infrastructure;

public record SseTrafficState(
        String deploymentColor,
        boolean shouldNotReceiveSse
) {

    public static final String BLUE = "blue";
    public static final String GREEN = "green";
    public static final String UNKNOWN = "unknown";

    public SseTrafficState {
        deploymentColor = normalizeDeploymentColor(deploymentColor);
    }

    public static SseTrafficState allowSse() {
        return new SseTrafficState(UNKNOWN, false);
    }

    public boolean isDeploymentColor(final String deploymentColor) {
        return this.deploymentColor.equals(normalizeDeploymentColor(deploymentColor));
    }

    private static String normalizeDeploymentColor(final String deploymentColor) {
        if (deploymentColor == null || deploymentColor.isBlank()) {
            return UNKNOWN;
        }

        return deploymentColor.trim().toLowerCase();
    }
}
