package com.dilsheen.ingest.api;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dilsheen.ingest.repo.InMemoryEventRepository;
import com.dilsheen.ingest.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EventControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new EventController(new EventService(new InMemoryEventRepository())))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void happyPathCreatesEvent() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"eventId":"e-100","type":"click","occurredAt":"2026-10-21T11:00:00Z","value":1.5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value("e-100"))
                .andExpect(jsonPath("$.type").value("click"))
                .andExpect(jsonPath("$.occurredAt").value("2026-10-21T11:00:00Z"))
                .andExpect(jsonPath("$.value").value(1.5));
    }

    @Test
    void invalidOccurredAtReturns400() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"eventId":"e-100","type":"click","occurredAt":"not-a-timestamp","value":1.5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("occurredAt"))
                .andExpect(jsonPath("$.details[0].issue").value("invalid_format"));
    }

    @Test
    void getReturnsStoredEvent() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"eventId":"e-100","type":"click","occurredAt":"2026-10-21T11:00:00Z","value":1.5}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/events/e-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("e-100"))
                .andExpect(jsonPath("$.type").value("click"))
                .andExpect(jsonPath("$.occurredAt").value("2026-10-21T11:00:00Z"))
                .andExpect(jsonPath("$.value").value(1.5));
    }

    @Test
    void getUnknownEventReturns404() throws Exception {
        mockMvc.perform(get("/v1/events/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"))
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void duplicateEventIdReturnsExisting() throws Exception {
        String body = """
                {"eventId":"e-100","type":"click","occurredAt":"2026-10-21T11:00:00Z","value":1.5}
                """;
        mockMvc.perform(post("/v1/events").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/events").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("e-100"))
                .andExpect(jsonPath("$.type").value("click"))
                .andExpect(jsonPath("$.occurredAt").value("2026-10-21T11:00:00Z"));
    }
}
