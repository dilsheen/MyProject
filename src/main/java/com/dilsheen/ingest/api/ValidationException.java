package com.dilsheen.ingest.api;

import com.dilsheen.ingest.api.dto.ErrorDetail;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<ErrorDetail> details;

    public ValidationException(String message, List<ErrorDetail> details) {
        super(message);
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public List<ErrorDetail> details() {
        return details;
    }
}
