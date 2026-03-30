package feedzupzup.backend.internal.controller;

import feedzupzup.backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalController {

    private final SseService sseService;

    @PostMapping("/internal/sse/disconnect")
    public ResponseEntity<Void> disconnectAllSse() {
        sseService.completeAllEmitters();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/sse/resume")
    public ResponseEntity<Void> resumeSse() {
        sseService.resumeAccepting();
        return ResponseEntity.ok().build();
    }

}
