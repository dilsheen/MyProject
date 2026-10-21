package com.dilsheen.ingest.api.dto;

import com.dilsheen.ingest.domain.Event;
import java.math.BigDecimal;

public record EventResponse(String eventId, String type, String occurredAt, BigDecimal value) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.eventId(),
                event.type(),
                event.occurredAt().toString(),
                event.value()
        );
    }
}
