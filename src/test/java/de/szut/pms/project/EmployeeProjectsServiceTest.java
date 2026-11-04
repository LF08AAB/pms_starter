package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
