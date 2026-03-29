package feedzupzup.backend.sse.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class SseException extends DomainException {

    public SseException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class SseConnectionRefusedException extends SseException {

        private static final ErrorCode errorCode = ErrorCode.SSE_CONNECTION_REFUSED;

        public SseConnectionRefusedException(String message) {
            super(errorCode, message);
        }
    }
}