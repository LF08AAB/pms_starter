package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeNotFoundException;
import de.szut.pms.employee.EmployeeServiceUnauthorizedException;
import de.szut.pms.employee.EmployeeServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        InOrder calls = inOrder(employeeClient, repository);
        calls.verify(employeeClient).verifyExists(2L);
        calls.verify(repository).findProjectsByEmployeeId(2L);
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
        verify(employeeClient).verifyExists(2L);
        verifyNoMoreInteractions(employeeClient);
    }

    @ParameterizedTest
    @MethodSource("employeeServiceErrors")
    void doesNotQueryDatabaseWhenEmployeeVerificationFails(RuntimeException error) {
        doThrow(error).when(employeeClient).verifyExists(2L);

        assertSame(error, assertThrows(error.getClass(), () -> service.getEmployeeProjects(2L)));

        verify(employeeClient).verifyExists(2L);
        verifyNoInteractions(repository);
    }

    static Stream<RuntimeException> employeeServiceErrors() {
        return Stream.of(
                new EmployeeNotFoundException(2L),
                new EmployeeServiceUnauthorizedException(),
                new EmployeeServiceUnavailableException(new IllegalStateException("Service unavailable")));
    }

    private void addAssignment(ProjectEntity project, Long employeeId, String qualification) {
        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
    }
}
