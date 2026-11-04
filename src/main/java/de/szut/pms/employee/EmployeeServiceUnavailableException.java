package de.szut.pms.employee;

public class EmployeeServiceUnavailableException extends RuntimeException {

    public EmployeeServiceUnavailableException(Throwable cause) {
        super("Der Employee-Service ist derzeit nicht erreichbar.", cause);
    }
}
