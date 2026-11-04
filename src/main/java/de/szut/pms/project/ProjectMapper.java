package de.szut.pms.project;


import org.springframework.stereotype.Component;
import java.util.HashSet;

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
        new HashSet<>(entity.getRequiredQualifications()),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getEmployees().stream()
                .map(employee -> new ProjectEmployeeDto(
                        employee.getEmployeeId(),
                        employee.getQualification()))
                .toList());
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
                dto.endDate());
    }

    public void updateEntity(ProjectEntity entity, PutProjectDto dto) {
        entity.setName(dto.name());
        entity.setResponsibleEmployeeId(dto.responsibleEmployeeId());
        entity.setCustomerId(dto.customerId());
        entity.setContactPerson(dto.contactPerson());
        entity.setComment(dto.comment());
        entity.getRequiredQualifications().clear();
        if (dto.requiredQualifications() != null) {
            entity.getRequiredQualifications().addAll(dto.requiredQualifications());
        }
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
    }
}
