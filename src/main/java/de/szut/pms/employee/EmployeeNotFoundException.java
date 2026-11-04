package de.szut.pms.employee;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long employeeId) {
        super("Es gibt keinen Mitarbeiter mit der Kennung " + employeeId + ".");
    }
}
