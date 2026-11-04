package de.szut.pms.project;

public class EmployeeAssignmentConflictException extends RuntimeException {

    public EmployeeAssignmentConflictException(Long employeeId, Long projectId) {
        super("Mitarbeiter " + employeeId
                + " ist im Zeitraum des Projekts " + projectId
                + " bereits einem anderen Projekt zugeordnet.");
    }
}
