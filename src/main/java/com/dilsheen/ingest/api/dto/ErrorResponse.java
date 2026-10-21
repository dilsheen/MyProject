package com.dilsheen.ingest.api.dto;

import java.util.List;

public record ErrorResponse(String error, String message, List<ErrorDetail> details) {

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, List.of());
    }
}
