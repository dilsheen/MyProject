package com.dilsheen.ingest.api.dto;

import java.math.BigDecimal;

public record EventRequest(String eventId, String type, String occurredAt, BigDecimal value) {
}
