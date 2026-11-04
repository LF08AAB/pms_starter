package de.szut.pms.project;

public class EmployeeAlreadyAssignedException extends RuntimeException {

    public EmployeeAlreadyAssignedException(Long projectId, Long employeeId) {
        super("Mitarbeiter " + employeeId + " ist Projekt " + projectId + " bereits zugeordnet.");
    }
}
