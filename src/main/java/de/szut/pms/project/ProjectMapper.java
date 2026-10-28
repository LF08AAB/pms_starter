package de.szut.pms.project;


import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectDto toDto(ProjectEntity entity) {
        return new ProjectDto(
        entity.getProjectId(),
        entity.getName(),
        entity.getResponsibleEmployeeId(),
        entity.getCustomerId(),
        entity.getContactPerson(),
        entity.getComment(),
        // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
        entity.getRequiredQualifications(),
        entity.getStartDate(),
        entity.getEndDate());
    }

    public ProjectEntity toEntity(CreateProjectDto dto) {

        return new ProjectEntity(
                dto.name(),
                dto.responsibleEmployeeId(),
                dto.customerId(),
                dto.contactPerson(),
                dto.comment(),
                // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
                dto.requiredQualifications(),
                dto.startDate(),
                dto.endDate());    }
}
