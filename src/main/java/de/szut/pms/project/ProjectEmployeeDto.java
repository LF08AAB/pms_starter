package de.szut.pms.project;

/** Ein Mitarbeiter im Projekt: seine Id und die Qualifikation, mit der er eingesetzt ist. */
public record ProjectEmployeeDto(
        Long employeeId,
        String qualification) {
}
