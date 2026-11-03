package de.szut.pms.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;

    public ProjectService(ProjectRepository repository, ProjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProjectDto create(CreateProjectDto dto) {
        ProjectEntity saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    @Transactional
    public ProjectDto update(Long id, PutProjectDto dto) {
        ProjectEntity entity = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
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
    public void deleteProject(Long id) {
        ProjectEntity project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        repository.delete(project);
    }
}
