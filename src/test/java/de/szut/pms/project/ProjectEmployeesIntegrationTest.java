package de.szut.pms.project;

import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeDetails;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectEmployeesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository repository;

    @MockitoBean
    private EmployeeClient employeeClient;

    @BeforeEach
    void cleanProjects() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser
    void returnsProjectAssignmentsAndQualificationCoverage() throws Exception {
        ProjectEntity project = createProject("Project", Set.of("Java", "SQL"));
        addAssignment(project, 2L, "Java");
        repository.saveAndFlush(project);
        when(employeeClient.findById(2L))
                .thenReturn(Optional.of(new EmployeeDetails(Set.of("Java", "Kotlin"))));

        mockMvc.perform(get("/project/{projectId}/employees", project.getProjectId()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "projectId": %d,
                          "name": "Project",
                          "employees": [{"employeeId": 2, "qualification": "Java"}],
                          "requiredQualifications": [
                            {"qualification": "Java", "covered": true},
                            {"qualification": "SQL", "covered": false}
                          ]
                        }
                        """.formatted(project.getProjectId()), JsonCompareMode.STRICT));
    }

    @Test
    @WithMockUser
    void returnsEmptyEmployeeListAndUncoveredQualifications() throws Exception {
        ProjectEntity project = repository.saveAndFlush(createProject("Empty", Set.of("Java")));

        mockMvc.perform(get("/project/{projectId}/employees", project.getProjectId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees").isEmpty())
                .andExpect(jsonPath("$.requiredQualifications[0].covered").value(false));
        verifyNoInteractions(employeeClient);
    }

    @Test
    @WithMockUser
    void returnsNotFoundForUnknownProject() throws Exception {
        mockMvc.perform(get("/project/{projectId}/employees", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Nicht gefunden"));
        verifyNoInteractions(employeeClient);
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/project/{projectId}/employees", 1L))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(employeeClient);
    }

    private ProjectEntity createProject(String name, Set<String> requiredQualifications) {
        return repository.saveAndFlush(new ProjectEntity(name, 1L, 10L, "Contact", "",
                requiredQualifications, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)));
    }

    private void addAssignment(ProjectEntity project, Long employeeId, String qualification) {
        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
    }
}
