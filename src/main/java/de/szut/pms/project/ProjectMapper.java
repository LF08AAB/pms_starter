package de.szut.pms.project;

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
}
