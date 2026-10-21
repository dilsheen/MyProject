package com.dilsheen.ingest.repo;

import com.dilsheen.ingest.domain.Event;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEventRepository implements EventRepository {

    private static final RowMapper<Event> ROW_MAPPER = (rs, rowNum) -> new Event(
            rs.getString("event_id"),
            rs.getString("type"),
            rs.getObject("occurred_at", OffsetDateTime.class).toInstant(),
            rs.getBigDecimal("value")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Event> findById(String eventId) {
        List<Event> rows = jdbcTemplate.query(
                "SELECT event_id, type, occurred_at, value FROM events WHERE event_id = ?",
                ROW_MAPPER,
                eventId
        );
        return rows.stream().findFirst();
    }

    @Override
    public Event insert(Event event) {
        jdbcTemplate.update(
                "INSERT INTO events (event_id, type, occurred_at, value) VALUES (?, ?, ?, ?)",
                event.eventId(),
                event.type(),
                Timestamp.from(event.occurredAt()),
                event.value()
        );
        return event;
    }
}
