package de.szut.pms.employee;

public class EmployeeServiceUnauthorizedException extends RuntimeException {

    public EmployeeServiceUnauthorizedException() {
        super("Der Employee-Service hat das Token abgelehnt. Bitte mit gültigem Bearer-Token anfragen.");
    }
}
