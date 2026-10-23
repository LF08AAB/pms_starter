package de.szut.pms.project;

import java.time.LocalDate;
import java.util.List;

/** Was der Service über ein Projekt zurückgibt: alle Daten inkl. der Mitarbeiter. */
public record ProjectResponseDto(
        Long id,
        String name,
        Long responsibleEmployeeId,
        Long customerId,
        String customerContactName,
        String comment,
        LocalDate startDate,
        LocalDate endDate,
        List<ProjectEmployeeDto> employees) {
}
