package de.szut.pms.project;

import de.szut.pms.common.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectValidationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(Mockito.mock(ProjectService.class)))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    private static String body(String name, String start, String end) {
        return """
                {"name": %s, "responsibleEmployeeId": 1, "customerId": 1,
                 "contactPerson": "Max", "comment": "c",
                 "requiredQualifications": ["Java"],
                 "startDate": %s, "endDate": %s}
                """.formatted(name, start, end);
    }

    private ResultActions doPost(String json) throws Exception {
        return mockMvc.perform(post("/project").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions doPut(String json) throws Exception {
        return mockMvc.perform(put("/project/1").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void post_missingRequiredFields_returns400() throws Exception {
        doPost("{}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").exists())
                .andExpect(jsonPath("$.errors.customerId").exists())
                .andExpect(jsonPath("$.errors.contactPerson").exists())
                .andExpect(jsonPath("$.errors.requiredQualifications").exists())
                .andExpect(jsonPath("$.errors.startDate").exists())
                .andExpect(jsonPath("$.errors.endDate").exists());
    }

    @Test
    void post_endBeforeStart_returns400() throws Exception {
        doPost(body("\"P\"", "\"2026-12-01\"", "\"2026-11-01\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dateRangeValid").exists());
    }

    @Test
    void post_invalidDate_returns400() throws Exception {
        doPost(body("\"P\"", "\"2026-13-45\"", "\"2027-01-01\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_blankName_returns400() throws Exception {
        doPost(body("\"  \"", "\"2026-11-01\"", "\"2027-01-01\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void put_missingRequiredFields_returns400() throws Exception {
        doPut("{}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void put_endBeforeStart_returns400() throws Exception {
        doPut(body("\"P\"", "\"2026-12-01\"", "\"2026-11-01\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dateRangeValid").exists());
    }

    @Test
    void put_invalidDate_returns400() throws Exception {
        doPut(body("\"P\"", "\"2026-11-01\"", "\"kein-datum\""))
                .andExpect(status().isBadRequest());
    }
}
