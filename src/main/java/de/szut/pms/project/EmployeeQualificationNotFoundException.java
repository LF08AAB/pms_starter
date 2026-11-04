package de.szut.pms.project;

public class EmployeeQualificationNotFoundException extends RuntimeException {

    public EmployeeQualificationNotFoundException(Long employeeId, String qualification) {
        super("Mitarbeiter " + employeeId + " besitzt nicht die Qualifikation "
                + qualification + ".");
    }
}
