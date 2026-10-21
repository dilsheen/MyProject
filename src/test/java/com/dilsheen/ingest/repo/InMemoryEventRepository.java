package com.dilsheen.ingest.repo;

import com.dilsheen.ingest.domain.Event;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DuplicateKeyException;

public class InMemoryEventRepository implements EventRepository {

    private final ConcurrentHashMap<String, Event> events = new ConcurrentHashMap<>();

    @Override
    public Optional<Event> findById(String eventId) {
        return Optional.ofNullable(events.get(eventId));
    }

    @Override
    public Event insert(Event event) {
        Event existing = events.putIfAbsent(event.eventId(), event);
        if (existing != null) {
            throw new DuplicateKeyException("duplicate eventId: " + event.eventId());
        }
        return event;
    }
}
