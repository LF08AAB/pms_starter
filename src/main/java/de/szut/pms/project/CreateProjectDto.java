package de.szut.pms.project;

import java.time.LocalDate;
import java.util.Set;

public record CreateProjectDto(
        String name,
        Long responsibleEmployeeId,
        Long customerId,
        String contactPerson,
        String comment,
        // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
        Set<String> requiredQualifications,
        LocalDate startDate,
        LocalDate endDate
) {
}
