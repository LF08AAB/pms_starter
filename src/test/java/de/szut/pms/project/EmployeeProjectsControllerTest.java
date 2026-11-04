package de.szut.pms.project;

import de.szut.pms.common.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeProjectsControllerTest {

    private ProjectService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(ProjectService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void returnsOkAndEmptyProjectsInsteadOfNotFound() throws Exception {
        when(service.getEmployeeProjects(2L)).thenReturn(new EmployeeProjectsDto(2L, List.of()));

        mockMvc.perform(get("/project/employees/{employeeId}", 2L))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"employeeId": 2, "projects": []}
                        """));
    }
}
