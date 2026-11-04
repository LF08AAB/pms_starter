package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeNotFoundException;
import de.szut.pms.employee.EmployeeServiceUnavailableException;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// T_02: POST /project
class ProjectCreateIntegrationTest extends AbstractIntegrationTest {

    private static final String VALID_BODY = body(1, 2, "\"2026-01-01\"", "\"2026-12-31\"");

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private EmployeeClient employeeClient;

    @MockitoBean
    private CustomerClient customerClient;

    @BeforeEach
    void prepare() {
        projectRepository.deleteAll();
        doNothing().when(employeeClient).verifyExists(anyLong());
        when(customerClient.existsById(anyLong())).thenReturn(true);
    }

    private static String body(long employeeId, long customerId, String start, String end) {
        return """
                {"name": "Testprojekt", "responsibleEmployeeId": %d, "customerId": %d,
                 "contactPerson": "Max Mustermann", "comment": "Kommentar",
                 "requiredQualifications": ["Java"],
                 "startDate": %s, "endDate": %s}
                """.formatted(employeeId, customerId, start, end);
    }

    private ResultActions doPost(String json) throws Exception {
        return mockMvc.perform(post("/project").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    @WithMockUser
    void create_returns201WithLocationAndBody() throws Exception {
        doPost(VALID_BODY)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.projectId").isNumber())
                .andExpect(jsonPath("$.name").value("Testprojekt"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.contactPerson").value("Max Mustermann"))
                .andExpect(jsonPath("$.requiredQualifications[0]").value("Java"))
                .andExpect(jsonPath("$.startDate").value("2026-01-01"))
                .andExpect(jsonPath("$.endDate").value("2026-12-31"));
    }

    @Test
    @WithMockUser
    void create_persistsProject() throws Exception {
        doPost(VALID_BODY).andExpect(status().isCreated());

        var projects = projectRepository.findAll();
        assertEquals(1, projects.size());
        ProjectEntity saved = projects.get(0);
        assertEquals("Testprojekt", saved.getName());
        assertEquals(1L, saved.getResponsibleEmployeeId());
        assertEquals(2L, saved.getCustomerId());
        assertTrue(projectRepository.existsById(saved.getProjectId()));
    }

    @Test
    @WithMockUser
    void create_validEmployeeId_returns201() throws Exception {
        doPost(VALID_BODY).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void create_unknownEmployeeId_returns404AndDoesNotPersist() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeClient).verifyExists(99L);

        doPost(body(99, 2, "\"2026-01-01\"", "\"2026-12-31\""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void create_employeeServiceUnavailable_returns503() throws Exception {
        doThrow(new EmployeeServiceUnavailableException(new RuntimeException()))
                .when(employeeClient).verifyExists(anyLong());

        doPost(VALID_BODY).andExpect(status().isServiceUnavailable());
        assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void create_invalidEmployeeId_returns400() throws Exception {
        doPost(body(-1, 2, "\"2026-01-01\"", "\"2026-12-31\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").exists());
    }

    @Test
    @WithMockUser
    void create_validCustomerId_returns201() throws Exception {
        doPost(VALID_BODY).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void create_unknownCustomerId_returns404AndDoesNotPersist() throws Exception {
        when(customerClient.existsById(77L)).thenReturn(false);

        doPost(body(1, 77, "\"2026-01-01\"", "\"2026-12-31\""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void create_invalidCustomerId_returns400() throws Exception {
        doPost(body(1, 0, "\"2026-01-01\"", "\"2026-12-31\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerId").exists());
    }

    // Die Projekt-ID wird serverseitig vergeben; eine mitgesendete ID darf kein Projekt überschreiben.
    @Test
    @WithMockUser
    void create_sameBodyTwice_createsTwoProjectsWithDistinctIds() throws Exception {
        doPost(VALID_BODY).andExpect(status().isCreated());
        doPost(VALID_BODY).andExpect(status().isCreated());

        var projects = projectRepository.findAll();
        assertEquals(2, projects.size());
        assertTrue(!projects.get(0).getProjectId().equals(projects.get(1).getProjectId()));
    }

    @Test
    @WithMockUser
    void create_withExistingProjectIdInBody_ignoresIdAndKeepsExistingProject() throws Exception {
        doPost(VALID_BODY).andExpect(status().isCreated());
        Long existingId = projectRepository.findAll().get(0).getProjectId();

        String withId = VALID_BODY.replaceFirst("\\{", "{\"projectId\": " + existingId + ",");
        doPost(withId).andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(org.hamcrest.Matchers.not(existingId.intValue())));

        assertEquals(2, projectRepository.count());
        assertEquals("Testprojekt", projectRepository.findById(existingId).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void create_invalidDateFormat_returns400() throws Exception {
        doPost(body(1, 2, "\"31.13.2026\"", "\"2026-12-31\""))
                .andExpect(status().isBadRequest());
        assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void create_nonExistingDate_returns400() throws Exception {
        doPost(body(1, 2, "\"2026-02-30\"", "\"2026-12-31\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_endBeforeStart_returns400() throws Exception {
        doPost(body(1, 2, "\"2026-12-31\"", "\"2026-01-01\""))
                .andExpect(status().isBadRequest());
        assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void create_malformedJson_returns400() throws Exception {
        doPost("{not json").andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_missingRequiredFields_returns400WithFieldErrors() throws Exception {
        doPost("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").exists())
                .andExpect(jsonPath("$.errors.customerId").exists())
                .andExpect(jsonPath("$.errors.contactPerson").exists())
                .andExpect(jsonPath("$.errors.requiredQualifications").exists())
                .andExpect(jsonPath("$.errors.startDate").exists())
                .andExpect(jsonPath("$.errors.endDate").exists());
        assertEquals(0, projectRepository.count());
    }

    @Test
    void create_withoutAuthentication_returns401() throws Exception {
        doPost(VALID_BODY).andExpect(status().isUnauthorized());
        assertEquals(0, projectRepository.count());
    }
}
