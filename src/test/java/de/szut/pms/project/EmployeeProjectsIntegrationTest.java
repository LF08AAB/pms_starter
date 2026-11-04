package de.szut.pms.project;

import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeProjectsIntegrationTest extends AbstractIntegrationTest {

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
    void includesCompletedCurrentAndFutureAssignedProjects() throws Exception {
        LocalDate today = LocalDate.now();
        ProjectEntity completed = createProject("Completed", 2L, "Java",
                today.minusYears(2), today.minusYears(1));
        ProjectEntity current = createProject("Current", 2L, "SQL",
                today.minusDays(1), today.plusDays(1));
        ProjectEntity future = createProject("Future", 2L, "Testing",
                today.plusYears(1), today.plusYears(2));

        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(2))
                .andExpect(jsonPath("$.projects[*].projectId", contains(
                        completed.getProjectId().intValue(),
                        current.getProjectId().intValue(),
                        future.getProjectId().intValue())))
                .andExpect(jsonPath("$.projects[0].endDate").value(today.minusYears(1).toString()));
    }

    @Test
    @WithMockUser
    void excludesDeletedProjectAndReturnsEmptyListAfterLastDeletion() throws Exception {
        LocalDate today = LocalDate.now();
        ProjectEntity deleted = createProject("Deleted", 2L, "Java", today, today.plusDays(1));
        ProjectEntity retained = createProject("Retained", 2L, "SQL", today, today.plusDays(1));

        mockMvc.perform(delete("/project/{id}", deleted.getProjectId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].projectId").value(retained.getProjectId()));

        mockMvc.perform(delete("/project/{id}", retained.getProjectId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(2))
                .andExpect(jsonPath("$.projects").isEmpty());
    }

    @Test
    @WithMockUser
    void returnsExactResponseWithRequestedQualificationAndLeader() throws Exception {
        ProjectEntity project = createProject("Project", 2L, "Java",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        project.setCustomerId(20L);
        project.setContactPerson("Internal contact");
        project.setComment("Internal comment");
        project.setRequiredQualifications(Set.of("SQL"));
        ProjectEmployeeEntity otherAssignment = new ProjectEmployeeEntity(3L, "Testing");
        otherAssignment.setProject(project);
        project.getEmployees().add(otherAssignment);
        repository.saveAndFlush(project);
        createProject("Other employee project", 3L, "Java",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "employeeId": 2,
                          "projects": [{
                            "projectId": %d,
                            "name": "Project",
                            "startDate": "2025-01-01",
                            "endDate": "2025-12-31",
                            "qualification": "Java",
                            "responsibleEmployeeId": 1
                          }]
                        }
                        """.formatted(project.getProjectId()), JsonCompareMode.STRICT));
    }

    @Test
    @WithMockUser
    void returnsEmptyProjectsWhenEmployeeIsOnlyProjectLeader() throws Exception {
        createProject("Other employee project", 3L, "Java", LocalDate.now(), LocalDate.now());

        mockMvc.perform(get("/project/employees/{employeeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"employeeId": 1, "projects": []}
                        """, JsonCompareMode.STRICT));
    }

    @Test
    @WithMockUser
    void returnsEmptyProjectsWhenDatabaseIsEmpty() throws Exception {
        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"employeeId": 2, "projects": []}
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(employeeClient);
    }

    @Test
    void rejectsInvalidBearerToken() throws Exception {
        mockMvc.perform(get("/project/employees/{employeeId}", 2L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(employeeClient);
    }

    @Test
    @WithMockUser
    void rejectsNonNumericEmployeeId() throws Exception {
        mockMvc.perform(get("/project/employees/not-a-number"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(employeeClient);
    }

    private ProjectEntity createProject(String name, Long employeeId, String qualification,
                                        LocalDate startDate, LocalDate endDate) {
        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        project.setResponsibleEmployeeId(1L);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
        return repository.saveAndFlush(project);
    }
}
