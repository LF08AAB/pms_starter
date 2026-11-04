package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeProjectsServiceTest {

    private ProjectRepository repository;
    private EmployeeClient employeeClient;
    private ProjectService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProjectRepository.class);
        employeeClient = mock(EmployeeClient.class);
        service = new ProjectService(repository, new ProjectMapper(),
                employeeClient, mock(CustomerClient.class));
    }

    @Test
    void returnsEmployeeIdAndEmptyProjectsWhenNoAssignmentsExist() {
        when(repository.findProjectsByEmployeeId(2L)).thenReturn(List.of());

        EmployeeProjectsDto response = service.getEmployeeProjects(2L);

        assertEquals(2L, response.employeeId());
        assertTrue(response.projects().isEmpty());
        verify(repository).findProjectsByEmployeeId(2L);
    }

    @Test
    void mapsOnlyRequestedEmployeesQualificationAndStoredLeader() {
        ProjectEntity project = new ProjectEntity("Project", 10L, 20L,
                "Contact", "Internal comment", Set.of("Required skill"),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        project.setProjectId(3L);
        addAssignment(project, 99L, "Other employee skill");
        addAssignment(project, 2L, "Java");
        when(repository.findProjectsByEmployeeId(2L)).thenReturn(List.of(project));

        EmployeeProjectsDto response = service.getEmployeeProjects(2L);

        assertEquals(new EmployeeProjectsDto(2L, List.of(new EmployeeProjectDto(
                3L, "Project", LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31), "Java", 10L))), response);
    }

    private void addAssignment(ProjectEntity project, Long employeeId, String qualification) {
        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
    }
}
