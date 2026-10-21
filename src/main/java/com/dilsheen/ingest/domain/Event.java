package com.dilsheen.ingest.domain;

import java.math.BigDecimal;
import java.time.Instant;

public record Event(String eventId, String type, Instant occurredAt, BigDecimal value) {
}
