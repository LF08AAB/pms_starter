package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeNotFoundException;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectUpdateIntegrationTest extends AbstractIntegrationTest {

    private static final String BODY = """
            {"name": "Neuer Name", "responsibleEmployeeId": 1, "customerId": 2,
             "comment": "Neuer Kommentar", "contactPerson": "Erika Muster",
             "requiredQualifications": ["Java"], "startDate": "2026-01-01", "endDate": "2026-12-31"}
            """;

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private EmployeeClient employeeClient;

    @MockitoBean
    private CustomerClient customerClient;

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
        when(customerClient.existsById(anyLong())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void updateProject_returns200AndPersistsChanges() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Alter Name");
        Long projectId = projectRepository.saveAndFlush(project).getProjectId();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.name").value("Neuer Name"));

        assertEquals("Neuer Name", projectRepository.findById(projectId).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void updateProject_returns404WhenProjectDoesNotExist() throws Exception {
        mockMvc.perform(put("/project/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateProject_validEmployeeId_isVerifiedAndAccepted() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        verify(employeeClient).verifyExists(1L);
    }

    @Test
    @WithMockUser
    void updateProject_unknownEmployeeId_returns404AndKeepsOldData() throws Exception {
        Long projectId = saveProject();
        doThrow(new EmployeeNotFoundException(1L)).when(employeeClient).verifyExists(1L);

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Mitarbeiter nicht gefunden"));

        assertEquals("Alter Name", projectRepository.findById(projectId).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void updateProject_invalidEmployeeId_returns400() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("\"responsibleEmployeeId\": 1", "\"responsibleEmployeeId\": -1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").value("Die Mitarbeiter-ID muss positiv sein."));
    }

    @Test
    @WithMockUser
    void updateProject_validCustomerId_isVerifiedAndAccepted() throws Exception {
        Long projectId = saveProject();
        when(customerClient.existsById(2L)).thenReturn(true);

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(2));

        verify(customerClient).existsById(2L);
    }

    @Test
    @WithMockUser
    void updateProject_unknownCustomerId_returns404AndKeepsOldData() throws Exception {
        Long projectId = saveProject();
        when(customerClient.existsById(2L)).thenReturn(false);

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Kunde nicht gefunden"));

        assertEquals("Alter Name", projectRepository.findById(projectId).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void updateProject_invalidCustomerId_returns400() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("\"customerId\": 2", "\"customerId\": 0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerId").value("Die Kunden-ID muss positiv sein."));
    }

    @Test
    @WithMockUser
    void updateProject_endBeforeStart_returns400AndKeepsOldData() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("\"startDate\": \"2026-01-01\", \"endDate\": \"2026-12-31\"",
                                "\"startDate\": \"2026-12-31\", \"endDate\": \"2026-01-01\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.dateRangeValid")
                        .value("Das Enddatum darf nicht vor dem Startdatum liegen."));

        assertEquals("Alter Name", projectRepository.findById(projectId).orElseThrow().getName());
    }

    @Test
    @WithMockUser
    void updateProject_invalidDateFormat_returns400() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("2026-01-01", "31.13.2026")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateProject_missingRequiredFields_returns400() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.responsibleEmployeeId").exists())
                .andExpect(jsonPath("$.errors.customerId").exists())
                .andExpect(jsonPath("$.errors.contactPerson").exists())
                .andExpect(jsonPath("$.errors.requiredQualifications").exists())
                .andExpect(jsonPath("$.errors.startDate").exists())
                .andExpect(jsonPath("$.errors.endDate").exists());
    }

    @Test
    @WithMockUser
    void updateProject_unknownProjectId_doesNotCallExternalServices() throws Exception {
        mockMvc.perform(put("/project/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verifyNoInteractions(employeeClient);
    }

    @Test
    void updateProject_returns401WithoutAuthentication() throws Exception {
        Long projectId = saveProject();

        mockMvc.perform(put("/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());

        assertEquals("Alter Name", projectRepository.findById(projectId).orElseThrow().getName());
    }

    private Long saveProject() {
        ProjectEntity project = new ProjectEntity();
        project.setName("Alter Name");
        return projectRepository.saveAndFlush(project).getProjectId();
    }
}
