package de.szut.pms.project;

<<<<<<< HEAD
import org.springframework.stereotype.Component;

/** Übersetzt zwischen DTOs (API) und Entities (Datenbank). */
@Component
public class ProjectMapper {

    /** Neues Projekt aus der Anfrage erzeugen (für POST). */
    public ProjectEntity toEntity(ProjectRequestDto dto) {
        ProjectEntity entity = new ProjectEntity();
        updateEntity(entity, dto);
        return entity;
    }

    /** Bestehendes Projekt mit den Daten der Anfrage überschreiben (für PUT). */
    public void updateEntity(ProjectEntity entity, ProjectRequestDto dto) {
        entity.setName(dto.name());
        entity.setResponsibleEmployeeId(dto.responsibleEmployeeId());
        entity.setCustomerId(dto.customerId());
        entity.setCustomerContactName(dto.customerContactName());
        entity.setComment(dto.comment());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
    }

    public ProjectResponseDto toDto(ProjectEntity entity) {
        return new ProjectResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getResponsibleEmployeeId(),
                entity.getCustomerId(),
                entity.getCustomerContactName(),
                entity.getComment(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getEmployees().stream()
                        .map(this::toDto)
                        .toList());
    }

    public ProjectEmployeeDto toDto(ProjectEmployeeEntity entity) {
        return new ProjectEmployeeDto(entity.getEmployeeId(), entity.getQualification());
    }
=======

import jakarta.persistence.ElementCollection;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
>>>>>>> feature-s01-t05
}
