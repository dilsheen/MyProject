package com.dilsheen.ingest.api;

import com.dilsheen.ingest.api.dto.EventRequest;
import com.dilsheen.ingest.api.dto.EventResponse;
import com.dilsheen.ingest.service.EventService;
import com.dilsheen.ingest.service.EventService.IngestResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/v1/events/{eventId}")
    public EventResponse get(@PathVariable String eventId) {
        return EventResponse.from(eventService.get(eventId));
    }

    @PostMapping("/v1/events")
    public ResponseEntity<EventResponse> ingest(@RequestBody EventRequest request) {
        IngestResult result = eventService.ingest(request);
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(EventResponse.from(result.event()));
    }
}
