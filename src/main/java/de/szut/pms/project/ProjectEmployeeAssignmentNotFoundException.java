package de.szut.pms.project;

public class ProjectEmployeeAssignmentNotFoundException extends RuntimeException {

    public ProjectEmployeeAssignmentNotFoundException(Long projectId, Long employeeId) {
        super("Mitarbeiter " + employeeId + " ist Projekt " + projectId + " nicht zugeordnet.");
    }
}
