package de.szut.pms.project;

import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeDetails;
import de.szut.pms.employee.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final EmployeeClient employeeClient;

    public ProjectService(
            ProjectRepository repository, ProjectMapper mapper, EmployeeClient employeeClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.employeeClient = employeeClient;
    }

    @Transactional
    public ProjectDto create(CreateProjectDto dto) {
        verifyEmployee(dto.responsibleEmployeeId());
        ProjectEntity saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProjectDto getById(Long projectId) {
        ProjectEntity project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return mapper.toDto(project);
    }

    @Transactional
    public ProjectDto update(Long id, PutProjectDto dto) {
        ProjectEntity entity = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        verifyEmployee(dto.responsibleEmployeeId());
        mapper.updateEntity(entity, dto);
        return mapper.toDto(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<ProjectEntity> projects = repository.findAll();

        return projects.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public ProjectDto assignEmployee(Long projectId, Long employeeId, String qualification) {
        ProjectEntity project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        EmployeeDetails employee = employeeClient.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        if (!employee.qualifications().contains(qualification)) {
            throw new EmployeeQualificationNotFoundException(employeeId, qualification);
        }
        if (project.getEmployees().stream()
                .anyMatch(assigned -> assigned.getEmployeeId().equals(employeeId))) {
            throw new EmployeeAlreadyAssignedException(projectId, employeeId);
        }

        ProjectEmployeeEntity assignment = new ProjectEmployeeEntity(employeeId, qualification);
        assignment.setProject(project);
        project.getEmployees().add(assignment);
        return mapper.toDto(repository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        ProjectEntity project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        repository.delete(project);
    }

    private void verifyEmployee(Long employeeId) {
        if (employeeId != null) {
            employeeClient.verifyExists(employeeId);
        }
    }
}
