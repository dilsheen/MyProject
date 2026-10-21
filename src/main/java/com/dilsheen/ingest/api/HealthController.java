package com.dilsheen.ingest.api;

import com.dilsheen.ingest.api.dto.StatusResponse;
import com.dilsheen.ingest.service.ReadinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final ReadinessService readinessService;

    public HealthController(ReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @GetMapping("/health")
    public StatusResponse health() {
        return new StatusResponse("up");
    }

    @GetMapping("/ready")
    public ResponseEntity<StatusResponse> ready() {
        if (readinessService.isReady()) {
            return ResponseEntity.ok(new StatusResponse("ready"));
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new StatusResponse("not_ready"));
    }
}
