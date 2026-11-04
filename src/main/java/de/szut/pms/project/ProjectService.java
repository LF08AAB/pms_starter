package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.customer.CustomerNotFoundException;
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
    private final CustomerClient customerClient;

    public ProjectService(ProjectRepository repository, ProjectMapper mapper, EmployeeClient employeeClient,
                          CustomerClient customerClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.employeeClient = employeeClient;
        this.customerClient = customerClient;
    }

    @Transactional
    public ProjectDto create(CreateProjectDto dto) {
        verifyEmployee(dto.responsibleEmployeeId());
        verifyCustomer(dto.customerId());
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
        verifyCustomer(dto.customerId());
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

    @Transactional(readOnly = true)
    public void validateEmployeeAssignment(Long projectId, Long employeeId, String qualification) {
        repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        EmployeeDetails employee = employeeClient.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        if (!employee.qualifications().contains(qualification)) {
            throw new EmployeeQualificationNotFoundException(employeeId, qualification);
        }
    }

    @Transactional
    public void deleteProject(Long id) {
        ProjectEntity project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        repository.delete(project);
    }

    @Transactional(readOnly = true)
    public EmployeeProjectsDto getEmployeeProjects(Long employeeId) {
        employeeClient.verifyExists(employeeId);
        List<EmployeeProjectDto> projects = repository.findProjectsByEmployeeId(employeeId).stream()
                .flatMap(project -> project.getEmployees().stream())
                .filter(assignment -> employeeId.equals(assignment.getEmployeeId()))
                .map(mapper::toEmployeeProjectDto)
                .toList();
        return new EmployeeProjectsDto(employeeId, projects);
    }

    @Transactional
    public ProjectDto removeEmployee(Long projectId, Long employeeId) {
        ProjectEntity project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        boolean removed = project.getEmployees().removeIf(
                employee -> employee.getEmployeeId().equals(employeeId));
        if (!removed) {
            throw new ProjectEmployeeAssignmentNotFoundException(projectId, employeeId);
        }
        ProjectEntity updatedProject = repository.saveAndFlush(project);
        return mapper.toDto(updatedProject);
    }

    private void verifyEmployee(Long employeeId) {
        if (employeeId != null) {
            employeeClient.verifyExists(employeeId);
        }
    }

    private void verifyCustomer(Long customerId) {
        if (!customerClient.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
