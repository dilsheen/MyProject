package com.dilsheen.ingest.repo;

import java.sql.Connection;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresHealthRepository {

    private static final Logger log = LoggerFactory.getLogger(PostgresHealthRepository.class);

    private final DataSource dataSource;

    public PostgresHealthRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean ping() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception ex) {
            log.warn("Postgres ping failed: {}", ex.getMessage());
            return false;
        }
    }
}
