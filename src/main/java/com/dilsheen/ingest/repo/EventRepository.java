package com.dilsheen.ingest.repo;

import com.dilsheen.ingest.domain.Event;
import java.util.Optional;

public interface EventRepository {

    Optional<Event> findById(String eventId);

    Event insert(Event event);
}
