package de.szut.pms.project;

import java.util.List;

public record EmployeeProjectsDto(
        Long employeeId,
        List<EmployeeProjectDto> projects
) {
}
