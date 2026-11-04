package de.szut.pms.project;

import java.time.LocalDate;

public record EmployeeProjectDto(
        Long projectId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String qualification
) {
}
