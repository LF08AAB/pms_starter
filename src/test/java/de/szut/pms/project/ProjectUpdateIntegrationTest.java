package de.szut.pms.project;

import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
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
    void updateProject_returns401WithoutAuthentication() throws Exception {
        mockMvc.perform(put("/project/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }
}
