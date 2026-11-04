package de.szut.pms.project;

import java.util.List;

public record ProjectEmployeesDto(
        Long projectId,
        String name,
        List<ProjectEmployeeDto> employees,
        List<ProjectQualificationDto> requiredQualifications
) {
}
