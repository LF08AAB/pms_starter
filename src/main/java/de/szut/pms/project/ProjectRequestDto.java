package de.szut.pms.project;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Was der Client beim Anlegen (POST) und Ändern (PUT) eines Projekts schickt.
 * Keine Id – die vergibt das System. Keine Mitarbeiterliste – Mitarbeiter
 * werden über eigene Endpunkte zugewiesen.
 */
public record ProjectRequestDto(

        @NotBlank(message = "Die Bezeichnung darf nicht leer sein.")
        @Size(max = 255, message = "Die Bezeichnung darf höchstens 255 Zeichen lang sein.")
        String name,

        @NotNull(message = "Der verantwortliche Mitarbeiter muss angegeben werden.")
        @Positive(message = "Die Mitarbeiter-Id muss positiv sein.")
        Long responsibleEmployeeId,

        @NotNull(message = "Die Kunden-Id muss angegeben werden.")
        @Positive(message = "Die Kunden-Id muss positiv sein.")
        Long customerId,

        @Size(max = 255, message = "Der Name des Ansprechpartners darf höchstens 255 Zeichen lang sein.")
        String customerContactName,

        @Size(max = 1000, message = "Der Kommentar darf höchstens 1000 Zeichen lang sein.")
        String comment,

        @NotNull(message = "Das Startdatum muss angegeben werden.")
        LocalDate startDate,

        @NotNull(message = "Das Enddatum muss angegeben werden.")
        LocalDate endDate) {

    /**
     * Feldübergreifende Prüfung: Enddatum darf nicht vor dem Startdatum liegen.
     * Fehlt eines der Daten, kümmern sich die @NotNull-Prüfungen darum.
     */
    @JsonIgnore
    @AssertTrue(message = "Das Enddatum darf nicht vor dem Startdatum liegen.")
    public boolean isEndDateNotBeforeStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
