package de.szut.pms.employee;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Es gibt keinen Mitarbeiter mit der Kennung " + id + ".");
    }
}
