
package de.szut.pms.project;

import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeDetails;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @MockitoBean
    private EmployeeClient employeeClient;

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
    }

    private ProjectEntity createTestProject(
            String name, Long employeeId, Long customerId) {

        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setResponsibleEmployeeId(employeeId);
        project.setCustomerId(customerId);
        project.setContactPerson("Max Mustermann");
        project.setComment("Integrationstest");
        project.setRequiredQualifications(new HashSet<>(Set.of("Java")));
        project.setStartDate(LocalDate.of(2026, 11, 1));
        project.setEndDate(LocalDate.of(2026, 12, 1));

        return projectRepository.save(project);
    }

    // T_05: Keine Projekte vorhanden
    @Test
    @WithMockUser
    void getAllProjects_returnsEmptyList_whenNoProjectsExist() throws Exception {
        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // T_06: Ein Projekt vorhanden
    @Test
    @WithMockUser
    void getAllProjects_returnsProjectWithAllFields() throws Exception {

        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);

        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(project.getProjectId()))
                .andExpect(jsonPath("$[0].name").value("Testprojekt"))
                .andExpect(jsonPath("$[0].responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$[0].customerId").value(2))
                .andExpect(jsonPath("$[0].contactPerson").value("Max Mustermann"))
                .andExpect(jsonPath("$[0].comment").value("Integrationstest"))
                .andExpect(jsonPath("$[0].requiredQualifications[0]").value("Java"))
                .andExpect(jsonPath("$[0].startDate").value("2026-11-01"))
                .andExpect(jsonPath("$[0].endDate").value("2026-12-01"));
    }

    // T_06: Mehrere Projekte vorhanden
    @Test
    @WithMockUser
    void getAllProjects_returnsAllStoredProjects() throws Exception {

        createTestProject("Projekt A", 1L, 2L);
        createTestProject("Projekt B", 3L, 4L);

        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.name == 'Projekt A')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Projekt B')]").exists());
    }

    // T_04: Kein JWT / keine Authentifizierung
    @Test
    void getAllProjects_returnsUnauthorizedWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/project"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void assignEmployee_persistsQualificationAndReturnsUpdatedProject() throws Exception {
        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);
        when(employeeClient.findById(10L))
                .thenReturn(Optional.of(new EmployeeDetails(Set.of("Java"))));

        mockMvc.perform(post("/project/{projectId}/employees/{employeeId}",
                        project.getProjectId(), 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"qualification":"Java"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(project.getProjectId()))
                .andExpect(jsonPath("$.employees[0].employeeId").value(10))
                .andExpect(jsonPath("$.employees[0].qualification").value("Java"));

        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employees[0].employeeId").value(10))
                .andExpect(jsonPath("$[0].employees[0].qualification").value("Java"));
    }

    @Test
    @WithMockUser
    void assignEmployee_returnsNotFoundWhenProjectDoesNotExist() throws Exception {
        mockMvc.perform(post("/project/{projectId}/employees/{employeeId}", 999L, 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"qualification":"Java"}
                                """))
                .andExpect(status().isNotFound());

        verify(employeeClient, never()).findById(10L);
    }

    @Test
    @WithMockUser
    void assignEmployee_returnsNotFoundWhenEmployeeDoesNotExist() throws Exception {
        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);
        when(employeeClient.findById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/project/{projectId}/employees/{employeeId}",
                        project.getProjectId(), 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"qualification":"Java"}
                                """))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employees").isEmpty());
    }

    @Test
    @WithMockUser
    void assignEmployee_rejectsQualificationEmployeeDoesNotHave() throws Exception {
        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);
        when(employeeClient.findById(10L))
                .thenReturn(Optional.of(new EmployeeDetails(Set.of("Java"))));

        mockMvc.perform(post("/project/{projectId}/employees/{employeeId}",
                        project.getProjectId(), 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"qualification":"Kotlin"}
                                """))
                .andExpect(status().isUnprocessableEntity());

        mockMvc.perform(get("/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employees").isEmpty());
    }

    @Test
    @WithMockUser
    void assignEmployee_rejectsBlankQualification() throws Exception {
        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);

        mockMvc.perform(post("/project/{projectId}/employees/{employeeId}",
                        project.getProjectId(), 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"qualification":" "}
                                """))
                .andExpect(status().isBadRequest());

        verify(employeeClient, never()).findById(10L);
    }
}
