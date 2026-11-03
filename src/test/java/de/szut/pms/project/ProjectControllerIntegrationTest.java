
package de.szut.pms.project;

import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

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

    @Test
    @WithMockUser
    void getById_returnsStoredProject() throws Exception {
        ProjectEntity project = createTestProject("Testprojekt", 1L, 2L);
        ProjectEmployeeEntity employee = new ProjectEmployeeEntity(3L, "Java");
        employee.setProject(project);
        project.getEmployees().add(employee);
        projectRepository.save(project);

        mockMvc.perform(get("/project/{projectId}", project.getProjectId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(project.getProjectId()))
                .andExpect(jsonPath("$.name").value("Testprojekt"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.contactPerson").value("Max Mustermann"))
                .andExpect(jsonPath("$.comment").value("Integrationstest"))
                .andExpect(jsonPath("$.requiredQualifications[0]").value("Java"))
                .andExpect(jsonPath("$.startDate").value("2026-11-01"))
                .andExpect(jsonPath("$.endDate").value("2026-12-01"))
                .andExpect(jsonPath("$.employees.length()").value(1))
                .andExpect(jsonPath("$.employees[0].employeeId").value(3))
                .andExpect(jsonPath("$.employees[0].qualification").value("Java"));
    }

    @Test
    @WithMockUser
    void create_thenGetById_preservesProjectFields() throws Exception {
        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Neues Projekt",
                                  "responsibleEmployeeId": 1,
                                  "customerId": 2,
                                  "contactPerson": "Erika Mustermann",
                                  "comment": "Projektziel",
                                  "requiredQualifications": ["Java"],
                                  "startDate": "2026-11-01",
                                  "endDate": "2026-12-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Neues Projekt"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(1))
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.contactPerson").value("Erika Mustermann"))
                .andExpect(jsonPath("$.comment").value("Projektziel"))
                .andExpect(jsonPath("$.requiredQualifications[0]").value("Java"))
                .andExpect(jsonPath("$.startDate").value("2026-11-01"))
                .andExpect(jsonPath("$.endDate").value("2026-12-01"));

        ProjectEntity created = projectRepository.findAll().get(0);
        mockMvc.perform(get("/project/{projectId}", created.getProjectId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Neues Projekt"))
                .andExpect(jsonPath("$.contactPerson").value("Erika Mustermann"))
                .andExpect(jsonPath("$.employees").isArray())
                .andExpect(jsonPath("$.employees").isEmpty());
    }

    @Test
    @WithMockUser
    void getById_returnsNotFound_whenProjectDoesNotExist() throws Exception {
        mockMvc.perform(get("/project/{projectId}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getById_returnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/project/{projectId}", 1L))
                .andExpect(status().isUnauthorized());
    }

    // T_04: Kein JWT / keine Authentifizierung
    @Test
    void getAllProjects_returnsUnauthorizedWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/project"))
                .andExpect(status().isUnauthorized());
    }
}
