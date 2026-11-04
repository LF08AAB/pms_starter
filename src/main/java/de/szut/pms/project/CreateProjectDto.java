package de.szut.pms.project;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record CreateProjectDto(
        @NotBlank(message = "Die Bezeichnung ist ein Pflichtfeld.")
        @Size(max = 255, message = "Die Bezeichnung darf höchstens 255 Zeichen lang sein.")
        String name,
        @NotNull(message = "Die Mitarbeiter-ID ist ein Pflichtfeld.")
        @Positive(message = "Die Mitarbeiter-ID muss positiv sein.")
        Long responsibleEmployeeId,
        @NotNull(message = "Die Kunden-ID ist ein Pflichtfeld.")
        @Positive(message = "Die Kunden-ID muss positiv sein.")
        Long customerId,
        @NotBlank(message = "Der Ansprechpartner ist ein Pflichtfeld.")
        @Size(max = 255, message = "Der Ansprechpartner darf höchstens 255 Zeichen lang sein.")
        String contactPerson,
        String comment,
        // klären, ob benötigte Qualifikationen als Namen oder Skill-IDs gespeichert werden sollen
        @NotEmpty(message = "Mindestens eine Qualifikation ist erforderlich.")
        Set<@NotBlank(message = "Qualifikationen dürfen nicht leer sein.") String> requiredQualifications,
        @NotNull(message = "Das Startdatum ist ein Pflichtfeld.")
        LocalDate startDate,
        @NotNull(message = "Das Enddatum ist ein Pflichtfeld.")
        LocalDate endDate
) {
    @AssertTrue(message = "Das Enddatum darf nicht vor dem Startdatum liegen.")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
