package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeDetails;
import de.szut.pms.employee.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ProjectEmployeesServiceTest {

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
    void returnsAssignedEmployeesAndMarksRequiredQualificationsCovered() {
        ProjectEntity project = projectWithEmployees();
        when(repository.findProjectWithEmployeesByProjectId(4L)).thenReturn(Optional.of(project));
        when(employeeClient.findById(2L))
                .thenReturn(Optional.of(new EmployeeDetails(Set.of("Java", "Kotlin"))));
        when(employeeClient.findById(3L))
                .thenReturn(Optional.of(new EmployeeDetails(Set.of("SQL"))));

        ProjectEmployeesDto response = service.getProjectEmployees(4L);

        assertEquals(4L, response.projectId());
        assertEquals("Project", response.name());
        assertEquals(List.of(
                new ProjectEmployeeDto(2L, "Java"),
                new ProjectEmployeeDto(3L, "SQL")), response.employees());
        assertEquals(List.of(
                new ProjectQualificationDto("Java", true),
                new ProjectQualificationDto("Kotlin", true),
                new ProjectQualificationDto("Rust", false)), response.requiredQualifications());
        verify(employeeClient).findById(2L);
        verify(employeeClient).findById(3L);
    }

    @Test
    void doesNotCallEmployeeServiceWhenProjectDoesNotExist() {
        when(repository.findProjectWithEmployeesByProjectId(4L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> service.getProjectEmployees(4L));

        verifyNoInteractions(employeeClient);
    }

    @Test
    void returnsNotFoundWhenAnAssignedEmployeeNoLongerExists() {
        ProjectEntity project = projectWithEmployees();
        when(repository.findProjectWithEmployeesByProjectId(4L)).thenReturn(Optional.of(project));
        when(employeeClient.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> service.getProjectEmployees(4L));
    }

    @Test
    void returnsEmptyEmployeesAndUncoveredRequirementsWithoutCallingEmployeeService() {
        ProjectEntity project = new ProjectEntity("Empty", 1L, 10L, "Contact", "",
                Set.of("Java"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        project.setProjectId(4L);
        when(repository.findProjectWithEmployeesByProjectId(4L)).thenReturn(Optional.of(project));

        ProjectEmployeesDto response = service.getProjectEmployees(4L);

        assertEquals(List.of(), response.employees());
        assertEquals(List.of(new ProjectQualificationDto("Java", false)),
                response.requiredQualifications());
        verifyNoInteractions(employeeClient);
    }

    private ProjectEntity projectWithEmployees() {
        ProjectEntity project = new ProjectEntity("Project", 1L, 10L, "Contact", "",
                Set.of("Java", "Kotlin", "Rust"),
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        project.setProjectId(4L);
        addAssignment(project, 2L, "Java");
        addAssignment(project, 3L, "SQL");
        return project;
    }

    private void addAssignment(ProjectEntity project, Long employeeId, String qualification) {
        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
    }
}
