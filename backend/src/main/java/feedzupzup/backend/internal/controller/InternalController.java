package feedzupzup.backend.internal.controller;

import feedzupzup.backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalController {

    private final SseService sseService;

    @GetMapping("/internal/sse/disconnect")
    public ResponseEntity<Void> disconnectAllSse() {
        sseService.completeAllEmitters();
        return ResponseEntity.ok().build();
    }

}
