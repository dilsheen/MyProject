package com.dilsheen.ingest.service;

import com.dilsheen.ingest.api.NotFoundException;
import com.dilsheen.ingest.api.ValidationException;
import com.dilsheen.ingest.api.dto.ErrorDetail;
import com.dilsheen.ingest.api.dto.EventRequest;
import com.dilsheen.ingest.domain.Event;
import com.dilsheen.ingest.repo.EventRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event get(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public IngestResult ingest(EventRequest request) {
        Event event = validate(request);
        return eventRepository.findById(event.eventId())
                .map(existing -> new IngestResult(existing, false))
                .orElseGet(() -> insertNew(event));
    }

    private IngestResult insertNew(Event event) {
        try {
            return new IngestResult(eventRepository.insert(event), true);
        } catch (DuplicateKeyException ex) {
            return eventRepository.findById(event.eventId())
                    .map(existing -> new IngestResult(existing, false))
                    .orElseThrow(() -> ex);
        }
    }

    private Event validate(EventRequest request) {
        if (request == null) {
            throw new ValidationException("Request body is required", List.of());
        }
        List<ErrorDetail> details = new ArrayList<>();
        if (isBlank(request.eventId())) {
            details.add(new ErrorDetail("eventId", "required"));
        }
        if (isBlank(request.type())) {
            details.add(new ErrorDetail("type", "required"));
        }
        Instant occurredAt = null;
        if (isBlank(request.occurredAt())) {
            details.add(new ErrorDetail("occurredAt", "required"));
        } else {
            try {
                occurredAt = parseUtc(request.occurredAt());
            } catch (RuntimeException ex) {
                details.add(new ErrorDetail("occurredAt", "invalid_format"));
            }
        }
        if (!details.isEmpty()) {
            throw new ValidationException("Request validation failed", details);
        }
        return new Event(request.eventId().trim(), request.type().trim(), occurredAt, request.value());
    }

    private static Instant parseUtc(String raw) {
        String value = raw.trim();
        try {
            return Instant.parse(value);
        } catch (Exception ignored) {
            // fall through
        }
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (Exception ignored) {
            // fall through
        }
        return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record IngestResult(Event event, boolean created) {
    }
}
