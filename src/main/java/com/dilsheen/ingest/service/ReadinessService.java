package com.dilsheen.ingest.service;

import com.dilsheen.ingest.repo.PostgresHealthRepository;
import org.springframework.stereotype.Service;

@Service
public class ReadinessService {

    private final PostgresHealthRepository postgresHealthRepository;

    public ReadinessService(PostgresHealthRepository postgresHealthRepository) {
        this.postgresHealthRepository = postgresHealthRepository;
    }

    public boolean isReady() {
        return postgresHealthRepository.ping();
    }
}
