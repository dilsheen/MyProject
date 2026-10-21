package com.dilsheen.ingest.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dilsheen.ingest.service.ReadinessService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class HealthControllerTest {

    @Test
    void healthReturnsUp() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new HealthController(new ReadinessService(null)))
                .build();

        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("up"));
    }
}
