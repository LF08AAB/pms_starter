
package de.szut.pms.project;

import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectDeleteIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void cleanProjects() {
        projectRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void deleteProject_returns204AndRemovesProject() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Testprojekt zum Löschen");
        project = projectRepository.saveAndFlush(project);

        Long projectId = project.getProjectId();

        mockMvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isNoContent());

        assertFalse(projectRepository.existsById(projectId));
    }

    @Test
    @WithMockUser
    void deleteProject_returns404WhenProjectDoesNotExist() throws Exception {
        mockMvc.perform(delete("/project/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Nicht gefunden"))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void deleteProject_returns401WithoutAuthentication() throws Exception {
        ProjectEntity project = new ProjectEntity();
        project.setName("Geschütztes Testprojekt");
        project = projectRepository.saveAndFlush(project);

        Long projectId = project.getProjectId();

        mockMvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isUnauthorized());

        assertTrue(projectRepository.existsById(projectId));
    }
}
